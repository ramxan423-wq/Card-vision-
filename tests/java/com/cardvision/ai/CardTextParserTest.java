package com.cardvision.ai;

/** Dependency-free parser regression tests: javac + java, no Android SDK required. */
public final class CardTextParserTest {
  private static int assertions;
  private static void eq(String expected, String actual, String label) {
    assertions++;
    if (!expected.equals(actual)) throw new AssertionError(label + ": expected [" + expected + "] but got [" + actual + "]");
  }
  public static void main(String[] args) {
    CardTextParser pokemon = CardTextParser.parse("BASIC\nPikachu\nHP 60\n025/102\nPokémon");
    eq("Pikachu", pokemon.name, "skip basic header");
    eq("025", pokemon.number, "fractional card number");
    eq("Pokémon", pokemon.category, "pokemon category");
    CardTextParser trainer = CardTextParser.parse("TRAINER\nProfessor's Research\nNo. 123\n2023");
    eq("Professor's Research", trainer.name, "skip trainer header");
    eq("123", trainer.number, "number with No. prefix");
    eq("2023", trainer.year, "year");
    CardTextParser full = CardTextParser.parse("STAGE 1\nCharmeleon\nHP 90\nTG12/TG30\nPokémon");
    eq("Charmeleon", full.name, "skip stage header");
    eq("TG12", full.number, "alphanumeric trainer gallery number");
    CardTextParser magicPlayer = CardTextParser.parse("Magic Johnson\n1987 NBA Hoops\n#32");
    eq("Magic Johnson", magicPlayer.name, "basketball player name");
    eq("", magicPlayer.brand, "magic is not automatically MTG brand");
    eq("Basketbol", magicPlayer.category, "NBA category");
    CardTextParser mtg = CardTextParser.parse("Magic: The Gathering\nPlaneswalker\nJace, the Mind Sculptor");
    eq("Magic", mtg.brand, "MTG brand from contextual evidence");
    eq("Jace, the Mind Sculptor", mtg.name, "skip MTG headers");
    eq("Magic: The Gathering", mtg.category, "MTG category");
    CardTextParser sports = CardTextParser.parse("Panini Prizm\nLeBron James\nCard No. 23\n2020 NBA");
    eq("Panini", sports.brand, "panini brand");
    eq("LeBron James", sports.name, "skip brand-only line");
    eq("23", sports.number, "card number label");
    eq("", CardTextParser.parse(null).name, "null-safe");
    eq("", CardTextParser.parse("HP 90\nBASIC\n2020").name, "avoid metadata as name");
    eq("", CardTextParser.parse("Selectively crafted").brand, "brand word boundaries");
    System.out.println("PASS: " + assertions + " CardTextParser assertions");
  }
}
