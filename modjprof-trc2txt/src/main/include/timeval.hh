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
#ifndef TIMEVAL_HH
#define TIMEVAL_HH

#include <iostream>
#include <iomanip>
#include <limits>
#include <cassert>
#include <sys/time.h>

using std::istream;
using std::ostream;
using std::setfill;
using std::setw;
using std::string;
using std::numeric_limits;

#ifdef TESTS
#define gettimeofday(...) _gettimeofday(__VA_ARGS__)
#endif

void _gettimeofday(struct timeval *tv, struct timezone *tz);

class TimeVal : public timeval
{
public:
	TimeVal();
	TimeVal(const timeval& tv);
	TimeVal(time_t sec, suseconds_t usec);
	TimeVal(unsigned long milliseconds);
	//explicit TimeVal(const string& time);
	TimeVal& operator-=(const TimeVal& other);
	TimeVal& operator+=(const TimeVal& other);
	TimeVal& operator/=(unsigned int divisor);
	double operator/=(const TimeVal& other);
	string industryFormat() const;
	string localTime() const;
	string localDate() const;

public:
	static TimeVal min();
	static TimeVal max();
	static TimeVal currentTime();
};


// inline functions

inline TimeVal::TimeVal()
{
	tv_sec = 0;
	tv_usec = 0;
}

inline TimeVal::TimeVal(const timeval& tv)
{
	*this = tv;
}

inline TimeVal::TimeVal(time_t sec, suseconds_t usec)
{
	tv_sec = sec;
	tv_usec = usec;
}

inline TimeVal::TimeVal(unsigned long milliseconds)
{
	tv_sec = milliseconds / 1000;
	tv_usec = milliseconds % 1000 * 1000;
}

inline TimeVal TimeVal::min()
{
	return TimeVal(numeric_limits<time_t>::min(), numeric_limits<suseconds_t>::max());
}

inline TimeVal TimeVal::max()
{
	return TimeVal(numeric_limits<time_t>::max(), numeric_limits<suseconds_t>::max());
}

inline TimeVal TimeVal::currentTime()
{
	TimeVal tv;
	gettimeofday(&tv, NULL);

	return tv;
}

inline bool operator<(const TimeVal& tv1, const TimeVal& tv2)
{
	return  (tv1.tv_sec < tv2.tv_sec) || (tv1.tv_sec == tv2.tv_sec && tv1.tv_usec < tv2.tv_usec);
}

inline bool operator>(const TimeVal& tv1, const TimeVal& tv2)
{
	return tv2 < tv1;
}

inline bool operator<=(const TimeVal& tv1, const TimeVal& tv2)
{
	return !(tv2 < tv1);
}

inline bool operator>=(const TimeVal& tv1, const TimeVal& tv2)
{
	return !(tv1 < tv2);
}

inline TimeVal operator-(const TimeVal& tv1, const TimeVal& tv2)
{
	assert(tv1 >= tv2);
	TimeVal result = tv1;
	return result -= tv2;
}

inline TimeVal operator+(const TimeVal& tv1, const TimeVal& tv2)
{
	TimeVal result = tv1;
	return result += tv2;
}

inline TimeVal operator/(const TimeVal& tv1, unsigned int divisor)
{
	TimeVal result = tv1;
	return result /= divisor;
}

inline double operator/(const TimeVal& tv1, const TimeVal& other)
{
	TimeVal result = tv1;
	return result /= other;
}

inline bool operator==(const TimeVal& tv1, const TimeVal& tv2)
{
	return (tv1.tv_sec == tv2.tv_sec) && (tv1.tv_usec == tv2.tv_usec);
}

inline bool operator!=(const TimeVal& tv1, const TimeVal& tv2)
{
	return !(tv1 == tv2);
}

inline ostream& operator<<(ostream& os, const TimeVal& tv)
{
	return os << tv.tv_sec << "." << setfill('0') << setw(6) << tv.tv_usec;
}

inline istream& operator>>(istream& is, TimeVal& tv)
{
	char period;
	return is >> tv.tv_sec >> period >> tv.tv_usec;
}

#endif

