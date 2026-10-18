package com.cardvision.ai;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Heuristic extraction from OCR. Never represents a verified catalogue match. */
public final class CardTextParser {
  public final String raw, name, year, number, brand, category;
  private CardTextParser(String raw,String name,String year,String number,String brand,String category){
    this.raw=raw;this.name=name;this.year=year;this.number=number;this.brand=brand;this.category=category;
  }
  public static CardTextParser parse(String input){
    String raw=input==null?"":input.trim();
    String year=match(raw,"\\b(?:19[5-9][0-9]|20[0-3][0-9])\\b");
    String number=match(raw,"(?i)(?:#|no\\.?\\s*|card\\s*#\\s*)([A-Z0-9-]{1,12})\\b");
    String[] brands={"Topps","Panini","Upper Deck","Bowman","Donruss","Prizm","Select","Optic","Fleer","Pokémon","Pokemon","Magic","Yu-Gi-Oh"};
    String brand="";
    for(String b:brands)if(raw.toLowerCase(Locale.ROOT).contains(b.toLowerCase(Locale.ROOT))){brand=b;break;}
    String category="Belirsiz";
    String low=raw.toLowerCase(Locale.ROOT);
    if(low.contains("pokemon")||low.contains("pokémon")||low.contains("hp "))category="Pokémon";
    else if(low.contains("yu-gi-oh")||low.contains("atk/")||low.contains("def/"))category="Yu-Gi-Oh!";
    else if(low.contains("magic: the gathering")||low.contains("planeswalker"))category="Magic: The Gathering";
    else if(low.contains("nba")||low.contains("basketball"))category="Basketbol";
    else if(low.contains("nfl")||low.contains("football"))category="Amerikan futbolu";
    else if(low.contains("mlb")||low.contains("baseball"))category="Beyzbol";
    String name="";
    for(String line:raw.split("\\R")){
      String v=line.trim();
      if(v.length()>2 && v.length()<65 && !v.matches(".*\\d{4}.*") && !v.matches("(?i)^(topps|panini|prizm|upper deck|bowman)$")){name=v;break;}
    }
    return new CardTextParser(raw,name,year,number,brand,category);
  }
  private static String match(String input,String regex){
    Matcher m=Pattern.compile(regex).matcher(input);
    if(!m.find())return "";
    return m.groupCount()>0?m.group(1):m.group();
  }
}
