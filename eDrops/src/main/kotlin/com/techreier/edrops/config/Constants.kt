package com.techreier.edrops.config

const val MAX_SEGMENT_SIZE = 30
const val MAX_STATE_SIZE = 20
const val MAX_TITLE_SIZE = 80
const val MAX_SUMMARY_SIZE = 1000
const val MAX_CODE_SIZE = 20
const val MAX_USERNAME_SIZE = 15
const val DEFAULT_TIMEZONE = "Europe/Oslo"
const val DOUBLE_FLOAT_PRECISION_DEFAULT = 5
const val DOUBLE_FIXED_PRECISION_DEFAULT = 2
const val MEDIA_URL_PATH = "/media"
const val PONG_TEXT = "Pong from TechReier"
const val SANITIZER = true
const val NEW_SEGMENT = "_new"
const val NEW_SUBSEGMENT = "_new"
const val BLOG_PUBLISHED_MIN_VALUE = 1
const val USE_COMMONMARK = true
const val SUBMENU_MIN_ITEMS = 2 //Minimum number of items within a topic sub menu
const val MENU_SPLIT_SIZE = 10 //Minimum menu size before splitting

// Graph constants
const val XSEGMENTS_MIN = 5
const val XSEGMENTS_MAX = 10
const val YSEGMENTS_MIN = 3
const val YSEGMENTS_MAX = 8
const val XSEGMENT_PIXELS = 100.0
const val YSEGMENT_PIXELS = 100.0
const val MAX_DECIMALS = 6
const val TICK_LENGTH = 12.0
const val SUBTICK_LENGTH = 10.0
const val X_LABEL_OFFSET = 10.0
const val Y_LABEL_OFFSET = 16.0
const val TOLERANCE = 1e-5 // for normalized floating point rounding Int
const val MIN_HEIGHT_RATIO = 0.2
const val MAX_HEIGHT_RATIO = 3.0
const val MIN_HEIGHT_RATIO_FOR_SUBTICS = 0.4
const val FONT_SCALE = 20.0
const val FONT_SCALE_EXPONENT = 0.25 // Ueed for Asic tick step: heightRatio.pow(FONT_SCALE_EXPONENT)
const val MIN_FONT_SCALE = 10.0
const val MAX_FONT_SCALE = 30.0
const val X_FONT_FACTOR = 0.6  //Relative space occupied by one character horizontally

// Drawing area constants
const val DIAGRAM_WIDTH = 700.0
const val PLOT_WIDTH = 600.0
const val PLOT_HEIGHT = 400.0
const val PLOT_ANCHOR_X  = 50.0
const val PLOT_ANCHOR_Y = 60.0
