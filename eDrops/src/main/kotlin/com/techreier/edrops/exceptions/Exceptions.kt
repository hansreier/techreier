package com.techreier.edrops.exceptions

import com.techreier.edrops.calc.ParseError

open class BlogException(msg: String, val key: String) : Exception(msg)

class BlogNotFoundException(msg: String) : BlogException(msg, "blogNotFound")

class PostNotFoundException(msg: String) : BlogException(msg, "postNotFound")

class TopicNotFoundException(msg: String) : BlogException(msg, "topicNotFound")

class DuplicateBlogException(msg: String) : BlogException(msg, "blogDuplicate")

class StateNotFoundException(msg: String) :BlogException(msg, "stateNotFound")

class NotAuthorizedException(msg: String) : BlogException(msg, "notAuthorized")

class ParentBlogException(msg: String) : Exception(msg)

class ParseException(msg: String, val parseError: ParseError) : Exception("$msg [Feiltype: $parseError]")

class CalcException(key: String, oper: String  = "", e: Exception? = null) : Exception("$key for $oper", e)