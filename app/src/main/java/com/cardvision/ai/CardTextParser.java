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
    String low=raw.toLowerCase(Locale.ROOT);
    String year=match(raw,"\\b(?:19[5-9][0-9]|20[0-3][0-9])\\b");
    // OCR frequently reads card numbers as "Card No. 12", "#12", or "12/250".
    String number=match(raw,"(?i)(?:\\bcard\\s*(?:no\\.?|number|#)|\\bno\\.?|\\bnumber|#)\\s*[:#-]?\\s*([A-Z0-9][A-Z0-9-]{0,11})\\b");
    if(number.isEmpty())number=match(raw,"(?i)\\b([A-Z]{0,4}[0-9]{1,4}[A-Z]?)\\s*/\\s*[A-Z]{0,4}[0-9]{1,4}\\b");
    String[] brands={"Topps","Panini","Upper Deck","Bowman","Donruss","Prizm","Select","Optic","Fleer","Pokémon","Pokemon","Magic","Yu-Gi-Oh"};
    String brand="";
    for(String b:brands){
      // "Magic Johnson" is a player, not evidence of the MTG brand.
      if(b.equals("Magic") && !(low.contains("magic: the gathering")||low.contains("planeswalker")||low.contains("wizards of the coast")||low.contains("mtg")))continue;
      Pattern p=Pattern.compile("(?iu)(?<![\\p{L}\\p{N}])"+Pattern.quote(b)+"(?![\\p{L}\\p{N}])");
      if(p.matcher(raw).find()){brand=b;break;}
    }
    String category="Belirsiz";
    if(low.contains("pokemon")||low.contains("pokémon")||low.contains("hp "))category="Pokémon";
    else if(low.contains("yu-gi-oh")||low.contains("atk/")||low.contains("def/"))category="Yu-Gi-Oh!";
    else if(low.contains("magic: the gathering")||low.contains("planeswalker"))category="Magic: The Gathering";
    else if(low.contains("nba")||low.contains("basketball"))category="Basketbol";
    else if(low.contains("nfl")||low.contains("football"))category="Amerikan futbolu";
    else if(low.contains("mlb")||low.contains("baseball"))category="Beyzbol";
    String name="";
    for(String line:raw.split("\\R")){
      String v=line.trim().replaceAll("\\s+"," ");
      if(v.length()<3||v.length()>64||!v.matches(".*[\\p{L}].*"))continue;
      if(v.matches(".*\\b(?:19[5-9][0-9]|20[0-3][0-9])\\b.*"))continue;
      if(v.matches("(?i)^(?:card\\s*)?(?:no\\.?|number|#)\\s*[:#-]?\\s*[a-z0-9/-]+$"))continue;
      if(v.matches("(?iu)^(?:basic(?:\\s+pok[eé]mon)?|stage\\s*[12]|trainer|supporter|item|stadium|energy|pok[eé]mon|rare|common|uncommon|holo(?:graphic)?|reverse\\s+holo|magic:\\s*the\\s*gathering|planeswalker|hp\\s*\\d{1,4}|\\d{1,4}\\s*hp|atk\\s*/\\s*\\d+|def\\s*/\\s*\\d+)$"))continue;
      if(v.matches("(?i)^(?:(?:topps|panini|prizm|upper|deck|bowman|donruss|select|optic|fleer|nba|nfl|mlb|pokemon|pokémon|yu-gi-oh)\\s*)+$"))continue;
      name=v;break;
    }
    return new CardTextParser(raw,name,year,number,brand,category);
  }
  private static String match(String input,String regex){
    Matcher m=Pattern.compile(regex).matcher(input);
    if(!m.find())return "";
    return m.groupCount()>0?m.group(1):m.group();
  }
}
