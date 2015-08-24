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
#ifndef IOMANIP_HH
#define IOMANIP_HH

using std::ios_base;

static const int INDENT = 1;

inline int indentIndex()
{
  static int index = ios_base::xalloc();

  return index;
}

inline ostream& setIndent( ostream& os, int indent )
{
  os.iword( indentIndex() ) = indent;

  return os;
}

inline int getIndent( ostream& os )
{
  return os.iword( indentIndex() );
}

#endif

