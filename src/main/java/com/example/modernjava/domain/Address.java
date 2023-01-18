package com.example.modernjava.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * Simple address class based loosely on Google's AddressData class
 * <a href="https://github.com/google/libaddressinput/blob/master/common/src/main/java/com/google/i18n/addressinput/common/AddressData.java">Google AddressData Class</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    String postalCountry;
    List<String> addressLines;
    String administrativeArea;
    String locality;
    String dependantLocality;
    String postalCode;
    String primaryPhoneNumber;
}
