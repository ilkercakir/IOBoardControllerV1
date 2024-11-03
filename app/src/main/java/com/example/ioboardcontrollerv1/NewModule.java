package com.example.ioboardcontrollerv1;

import java.io.Serializable;

public class NewModule implements Serializable
{
    private Integer modul, mtype;
    private String modtx, categ, catxt;

    public NewModule(Integer module, String moduleText, Integer moduleType, String category, String categoryText)
    {
        this.modul = module;
        this.modtx = moduleText;
        this.mtype = moduleType;
        this.categ = category;
        this.catxt = categoryText;
    }

    public Integer getModule() { return this.modul; }
    public Integer getModuleType() { return this.mtype; }
    public String getModuleText() { return this.modtx; }
    public String getCategory() { return this.categ; }
    public String getCategoryText() { return this.catxt; }
}
