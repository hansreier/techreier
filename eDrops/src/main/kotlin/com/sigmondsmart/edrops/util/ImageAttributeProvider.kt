package com.sigmondsmart.edrops.util

import org.commonmark.node.Image
import org.commonmark.node.Node
import org.commonmark.renderer.html.AttributeProvider
// https://github.com/commonmark/commonmark-java

class ImageAttributeProvider : AttributeProvider {
    override fun setAttributes(node: Node?, tagName: String?, attributes: MutableMap<String?, String?>) {
        if (node is Image) {
            attributes["width"] = "500"
        }
    }
}