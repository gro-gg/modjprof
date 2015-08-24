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
#include <sstream>
#include <limits>
#include <cassert>
#include <cstring>

#include "timeval.hh"

using std::fixed;
using std::setprecision;
using std::setfill;
using std::stringstream;
using std::numeric_limits;

TimeVal& TimeVal::operator-=(const TimeVal& other)
{
	tv_sec -= other.tv_sec;
	tv_usec -= other.tv_usec;

	if (tv_usec < 0)
	{
		tv_sec--;
		tv_usec += 1000000;
	}

	return *this;
}

TimeVal& TimeVal::operator+=(const TimeVal& other)
{
	tv_sec += other.tv_sec;
	tv_usec += other.tv_usec;

	if (tv_usec >= 1000000)
	{
		tv_sec++;
		tv_usec -= 1000000;
	}

	return *this;
}

TimeVal& TimeVal::operator/=(unsigned int divisor)
{
	assert(divisor != 0);
	tv_usec += (tv_sec % divisor) * 1000000;
	tv_sec /= divisor;
	tv_usec /= divisor;

	if (tv_usec >= 1000000)
	{
		assert(false);
		//tv_sec++;
		//tv_usec -= 1000000;
	}

	return *this;
}

double TimeVal::operator/=(const TimeVal& other)
{
	return (tv_sec + tv_usec / 1E6) / (other.tv_sec + other.tv_usec / 1E6);
}

string TimeVal::industryFormat() const
{
	string unit = "Ih";
	double time = ( tv_sec + tv_usec / 1E6 ) / 0.36 / 1E4;
	/*if (time < 100)
	{
		unit = "Is";
	}
	else if (time < 10000)
	{
		unit = "Im";
	}*/
	stringstream sstream;
	sstream << fixed << setprecision(2) << time; // << " " << unit;
	//tv.tv_sec << "." << setfill('0') << setw(6) << tv.tv_usec;
	//stringstream sstream;
	//sstream << setfill('0') << setw(6) << endl;

	return sstream.str();
}

string TimeVal::localTime() const
{
	if (tv_sec == numeric_limits<time_t>::min() || tv_sec == numeric_limits<time_t>::max()) return "";

	tm* localTm = localtime(&tv_sec);
	stringstream sstream;
	sstream << setfill('0') << setw(2) << localTm->tm_hour << ":" << setw(2) << localTm->tm_min; // << ":" << localTm->tm_sec;
	return sstream.str();
}

string TimeVal::localDate() const
{
	struct tm* tm = localtime(&tv_sec);
	stringstream result;

	result << 1900 + tm->tm_year << "-" << setfill('0') << setw(2) << tm->tm_mon + 1 << "-" << setw(2) << tm->tm_mday;

	return result.str();
}
