package com.myapp.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root(strict = false) // only works in non-strict mode
public class LangTranslator {

        @Text(required = false) // will be null in forged POJO object
        private String text;    // if empty
        @Attribute(name = "xmlns", required = false)
        private int id;
        public LangTranslator() {} // empty constructor required

    public String getText() {
        return text;
    }

    public int getId() {
        return id;
    }

}
