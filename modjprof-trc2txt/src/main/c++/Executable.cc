/**
 * Copyright 2015 The modjprof Project Developers. See the COPYRIGHT file at the top-level directory of this distribution and at
 * https://github.com/gro-gg/modjprof/blob/master/COPYRIGHT.
 *
 * This file is part of modjprof Project. It is subject to the license terms in the LICENSE file found in the top-level directory
 * of this distribution and at https://github.com/gro-gg/modjprof/blob/master/LICENSE. No part of modjprof Project, including this
 * file, may be copied, modified, propagated, or distributed except according to the terms contained in the LICENSE file.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See LICENSE file for more details.
 */
#include <cmath>
#include <fstream>
#include <sstream>
#include <vector>
#include <stack>
#include <algorithm>

#include "timeval.hh"
#include "iomanip.hh"

using std::fstream;
using std::istream;
using std::stringstream;
using std::ios_base;
using std::getline;
using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::vector;
using std::stack;
using std::sort;
using std::mem_fun;
using std::bind1st;
using std::replace;
using std::find;

void convertType(istream& inStream, ostream& outStream)
{
	char ch = 0;
	inStream >> ch;
	switch (ch)
	{
		case 'V':
			outStream << "void";
		case 'Z':
			outStream << "boolean";
			break;
		case 'B':
			outStream << "byte";
			break;
		case 'C':
			outStream << "char";
			break;
		case 'S':
			outStream << "short";
			break;
		case 'I':
			outStream << "int";
			break;
		case 'J':
			outStream << "long";
			break;
		case 'F':
			outStream << "float";
			break;
		case 'D':
			outStream << "double";
			break;
		case 'L':
			{
				string cls;
				getline(inStream, cls, ';');
				//cls = cls.substr(cls.rfind('/') + 1);
				replace(cls.begin(), cls.end(), '/', '.');
				outStream << cls;
				break;
			}
		case '[':
			{
				int dimension = 1;
				while (inStream.peek() == '[')
				{
					inStream.get();
					dimension++;
				}
				convertType(inStream, outStream);
				while (dimension--) outStream << "[]";
				break;
			}
		case '(':
			{
				string args;
				getline(inStream, args, ')');
				stringstream dummy;
				convertType(inStream, dummy);
				outStream << "(";
				stringstream sstream(args);
				convertType(sstream, outStream);
				while (sstream.peek() != stringstream::traits_type::eof())
				{
					outStream << ", ";
					convertType(sstream, outStream);
				}
				outStream << ")";
				break;
			}
	}
}

string convertSignature(const string& jvmType)
{
	stringstream istream(jvmType, ios_base::in);
	stringstream ostream;
	while (!istream.eof())
	{
		convertType(istream, ostream);
	}

	return ostream.str();
}

class Call
{
public:
	Call();
	~Call();
	void end(const TimeVal& startTime, const TimeVal& endTime);
	ostream& toXml(ostream& os) const;
	bool operator==(const Call& other) const;
	bool operator!=(const Call& other) const;
	void sort();

	string cls;
	string method;
	string parameters;
	//string sql;
	TimeVal totalTime;
	TimeVal selfTime;
	int totalPercent;
	int count;
	vector<Call*> _calls;
};

void Call::end(const TimeVal& startTime, const TimeVal& endTime)
{
	count++;
	if (startTime < endTime)
	{
		totalTime += endTime - startTime;
	}
	TimeVal childTime;
	vector<Call*>::iterator it = _calls.begin();
	while (it != _calls.end())
	{
		childTime += (*it)->totalTime;
		it++;
	}

	it = _calls.begin();
	while (it != _calls.end())
	{
		(*it)->totalPercent = (int) rint(((*it)->totalTime / childTime) * 100.0);
		it++;
	}

	if (totalTime != 0)
	{
		selfTime = totalTime - childTime;
	}
}

inline bool Call::operator==(const Call& other) const
{
	return method == other.method && parameters == other.parameters;
}

inline bool Call::operator!=(const Call& other) const
{
	return !(*this == other);
}

inline bool callTimeOrdering(const Call* call1, const Call* call2)
{
	return call1->totalTime > call2->totalTime;
}

inline void Call::sort()
{
	::sort(_calls.begin(), _calls.end(), callTimeOrdering);
	for_each(_calls.begin(), _calls.end(), mem_fun(&Call::sort));
}

inline istream& operator>>(istream& is, Call& call)
{
	return is >> call.cls >> call.method >> call.parameters;
}

ostream& operator<<(ostream& os, const Call& call)
{
	//call.cls = call.cls.substr(call.cls.rfind('.') + 1);

	int indent = getIndent(os);
	//call.totalTime / call.count
	if (call.cls != "sql")
	{
		os << string(indent, '\t') << "total: " << call.totalTime << " (" << call.totalPercent << "%)  self: " << call.selfTime << "  count: " << call.count << "  avg: " << call.totalTime / call.count << "  " << convertSignature(call.cls) << "." << call.method <<  convertSignature(call.parameters) << endl;
	}
	else
	{
		os << string(indent, '\t') << "total: " << call.totalTime << " (" << call.totalPercent << "%)  self: " << call.selfTime << "  count: " << call.count << "  avg: " << call.totalTime / call.count << "  sql:" << call.method << endl;
	}
	/*if (!call.sql.empty())
	{
		os << string(indent + 1, '\t') << "sql: " << call.sql << endl;
	}	*/
	setIndent(os, indent + 1);
	vector<Call*>::const_iterator it = call._calls.begin();
	while (it != call._calls.end())
	{
		os << **it++;
	}
	setIndent(os, indent);
	return os;
}

/*ostream& Call::toXml(ostream& os) const
{
	os << "<call total=\"" << totalTime << "\" class=\"" <<  cls << "\" method=\"" << method << parameters;
	if (_calls.empty())
	{
		os << "\"/>" << endl;
	}
	else
	{
		os << "\">" << endl;
		vector<Call*>::const_iterator it = _calls.begin();
		while (it != _calls.end())
		{
			(*it)->toXml(os);
			it++;
		}
		os << "</call>" << endl;

	}

	return os;
}*/


inline Call::Call() : count(0), totalPercent(0)
{
}

inline Call::~Call()
{
	vector<Call*>::const_iterator it = _calls.begin();
	while (it != _calls.end())
	{
		delete *it++;
	}		
}


int main()
{
	Call rootCall;
	stack<Call*> callStack;
	stack<Call*> lastNajsCallStack;
	stack<TimeVal> startTimeStack;
	callStack.push(&rootCall);
	//fstream traceStream("/var/tmp/rsjprof.trc", ios_base::in);
	istream& traceStream(cin);
	Call* lastNajsCall = 0;
	TimeVal endTime;
	while (!traceStream.eof())
	{
		char ch = 0;
		traceStream >> ch;
		switch (ch)
		{
			case '>':
				{
					Call* parentCall = callStack.top();
					TimeVal startTime;
					Call* call = new Call;
					//string sql;
					traceStream >> startTime >> *call;
					//getline(traceStream, sql);
					/*if (!sql.empty())
					{
						if (lastNajsCall)
						{
							if (!lastNajsCall->sql.empty()) lastNajsCall->sql += "\n";
							lastNajsCall->sql += sql;
						}
						else
						{
							if (!call->sql.empty()) call->sql += "\n\t";
							call->sql += sql;														
						}
					}*/									
					vector<Call*>::const_iterator it = parentCall->_calls.begin();
					while (it != parentCall->_calls.end() && **it != *call) it++;
					startTimeStack.push(startTime);
					lastNajsCallStack.push(lastNajsCall);
					if (it == parentCall->_calls.end())
					{
						parentCall->_calls.push_back(call);										
					}
					else
					{
						delete call;
						call = *it;												
					}
					callStack.push(call);
					//if (call->cls.compare(0, 5, "Lduke") == 0 || call->cls.compare(0,5,"Lnajs") == 0)
					/*if (call->cls.compare(0, 14, "Lorg/hibernate") == 0)
					{						
						lastNajsCall = call;
					}	*/									
					break;
				}
			case '<':
				{
					if (!startTimeStack.empty())
					{
						Call* call = callStack.top();
						lastNajsCall = lastNajsCallStack.top();
						TimeVal& startTime = startTimeStack.top();
						endTime = 0;
						traceStream >> endTime;
						call->end(startTime, endTime);
						callStack.pop();
						startTimeStack.pop();
						lastNajsCallStack.pop();
						break;
					}
				}
			case 's':
				{
					TimeVal elapsedTime;
					string sql;
					traceStream >> elapsedTime;
					getline(traceStream, sql);
					if (!callStack.empty())
					{
						Call* parentCall = callStack.top();
						Call* call = new Call;
						call->cls = "sql";
						call->method = sql;						
						vector<Call*>::const_iterator it = parentCall->_calls.begin();
						while (it != parentCall->_calls.end() && **it != *call) it++;					
						if (it == parentCall->_calls.end())
						{
							parentCall->_calls.push_back(call);										
						}
						else
						{
							delete call;
							call = *it;							
						}
						call->end(0, elapsedTime);
					}
					break;
				}
		}

	}

	if (!startTimeStack.empty()) cerr << "Warning: imcomplete trace!" << endl;
	while (!startTimeStack.empty())
	{
		Call* call = callStack.top();
		lastNajsCall = lastNajsCallStack.top();
		TimeVal& startTime = startTimeStack.top();
		call->end(startTime, endTime);
		callStack.pop();
		startTimeStack.pop();
		lastNajsCallStack.pop();		
	}	
	
	rootCall.end(TimeVal(), TimeVal());
	rootCall.sort();

	cout << ":folding=indent:collapseFolds=1:tabSize=2:indentSize=2:" << endl;
	cout << "vim:foldmethod=indent:tabstop=2:shiftwidth=2" << endl;

	//cout << "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" << endl;
	//cout << "<rsjprof>" << endl;
	vector<Call*>::const_iterator it = rootCall._calls.begin();
	while (it != rootCall._calls.end())
	{
		//(*it).toXml(cout);
		cout << **it++;
	}
	//cout << endl;
	//cout << "</rsjprof>" << endl;

	return 0;
}

