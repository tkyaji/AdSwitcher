//
//  Log.h
//  AdSwitcher
//
//  Created by tkyaji on 2016/07/04.
//  Copyright © 2016年 adwitcher. All rights reserved.
//

#ifndef Log_h
#define Log_h

#if defined(DEBUG) || defined(ADSWITCHER_DEBUG)
#define ENABLE_DEBUG_LOG
#endif

#define ENABLE_ERROR_LOG

#ifdef ENABLE_DEBUG_LOG
#define _DLOG(fmt, ...) NSLog((@"%s(L%d) " fmt), __PRETTY_FUNCTION__, __LINE__, ##__VA_ARGS__);
#else
#define _DLOG(...)
#endif

#ifdef ENABLE_ERROR_LOG
#define _ELOG(...) NSLog(__VA_ARGS__)
#else
#define _ELOG(...)
#endif

#endif /* Log_h */
