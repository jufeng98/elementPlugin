package org.javamaster.elementui.support

/**
 * @author yudong
 */
class ElementUIComponent {
    var name: String = ""
    var attributes: List<ElementUIComponentAttr> = mutableListOf()
    var props: List<ElementUIComponentProp> = mutableListOf()
    var options: List<ElementUIComponentOption> = mutableListOf()
    var shortcuts: List<ElementUIComponentShortcut> = mutableListOf()
    var slots: List<ElementUIComponentSlot> = mutableListOf()
    var events: List<ElementUIComponentEvent> = mutableListOf()
    var methods: List<ElementUIComponentMethod> = mutableListOf()
}

open class ElementUIComponentAttr {
    var name: String = ""
    var desc: String = ""
    var type: String = ""
    var optionValue: String = ""
    var options: List<String> = mutableListOf()
    var defaultValue: String = ""
}

class ElementUIComponentProp : ElementUIComponentAttr()

class ElementUIComponentOption : ElementUIComponentAttr()

class ElementUIComponentShortcut : ElementUIComponentAttr()

class ElementUIComponentSlot {
    var name: String = ""
    var desc: String = ""
}

open class ElementUIComponentEvent {
    var name: String = ""
    var desc: String = ""
    var param: String = ""
}

class ElementUIComponentMethod : ElementUIComponentEvent()