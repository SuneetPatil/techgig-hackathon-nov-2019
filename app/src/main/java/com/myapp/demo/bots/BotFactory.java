//
//
//
// Copyright 2017 Amazon.com, Inc. or its affiliates (Amazon). All Rights Reserved.
//
// Code generated by AWS Mobile Hub. Amazon gives unlimited permission to 
// copy, distribute and modify it.
//
// Source code generated from template: aws-my-sample-app-android v0.18
//

package com.myapp.demo.bots;

import java.util.ArrayList;
import java.util.List;

public class BotFactory {

    private static BotFactory instance;

    private BotFactory(){

    }

    public static BotFactory Instance(){
        if(instance==null)
            instance= new BotFactory();

        return instance;
    }

    public List<Bot> getBots(){
        List<Bot> botList = new ArrayList<Bot>();
                
        botList.add(new Bot("Bot Name",
                            "Bot Alias",
                            "",
                            "region",
                            new String[]{
                                  }));
        

        return botList;
    }



}
