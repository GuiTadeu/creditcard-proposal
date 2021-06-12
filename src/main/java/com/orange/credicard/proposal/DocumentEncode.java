package com.orange.credicard.proposal;

import java.util.Arrays;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class DocumentEncode {

    public static String[] encryptCharacters = {"0", "R", "4", "N", "G", "3", "2", "O", "S", "1"};

    public static String simpleEncode(String plainDocument) {
        String onlyNumbers = plainDocument.replaceAll("[^\\d ]", "");

        return Arrays.stream(onlyNumbers.split(""))
            .map(Integer::valueOf)
            .map(index -> encryptCharacters[index])
            .collect(Collectors.joining());
    }

    public static String simpleDecode(String encryptDocument) {
        return Arrays.stream(encryptDocument.split(""))
            .map(character -> asList(encryptCharacters).indexOf(character))
            .map(String::valueOf)
            .collect(Collectors.joining());
    }
}
