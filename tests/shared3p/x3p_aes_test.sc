/*
 * This file is a part of the Sharemind framework.
 * Copyright (C) Cybernetica AS
 *
 * All rights are reserved. Reproduction in whole or part is prohibited
 * without the written consent of the copyright owner. The usage of this
 * code is subject to the appropriate license agreement.
 */

module shared3p_aes_test;

import stdlib;
import matrix;
import shared3p;
import shared3p_matrix;
import oblivious;
import shared3p_random;
import shared3p_sort;
import shared3p_bloom;
import shared3p_string;
import shared3p_aes;
import shared3p_join;
import profiling;
import test_utility;

domain pd_shared3p shared3p;

public uint all_tests;
public uint succeeded_tests;


void main(){
    print("S3P_AES test: start");

    print("TEST 1: aes128 key generation");
    {
        for(uint i = 50; i < 300; i = i + 50){
            pd_shared3p xor_uint32[[1]] key = aes128Genkey(i);

            if(size(key) == (i * 4)){
                succeeded_tests = succeeded_tests + 1;
                all_tests = all_tests +1;
                print("SUCCESS!");
            }
            else{
                print("FAILURE! aes128 key generation failed, key too big or too small");
                all_tests = all_tests +1;
            }
        }
    }
    print("TEST 2: aes128 key expansion");
    {
        for(uint i = 2; i <= 10; i = i + 2){
            pd_shared3p xor_uint32[[1]] key = aes128Genkey(i);
            pd_shared3p xor_uint32[[1]] expandedKey = aes128ExpandKey(key);

            if(size(expandedKey) == (i * 44)){
                succeeded_tests = succeeded_tests + 1;
                all_tests = all_tests +1;
                print("SUCCESS!");
            }
            else{
                print("FAILURE! aes128 expanded key generation failed, key too big or too small");
                all_tests = all_tests +1;
            }
        }
    }
    print("TEST 3: Encrypt with aes128");
    {
        pd_shared3p xor_uint32[[1]] plainText = {0x3243f6a8, 0x885a308d, 0x313198a2, 0xe0370734};
        pd_shared3p xor_uint32[[1]] cipherText1 = {0x3925841d, 0x2dc09fb, 0xdc118597, 0x196a0b32};
        pd_shared3p xor_uint32[[1]] expandedKey = {
            0x2b7e1516, 0x28aed2a6, 0xabf71588, 0x09cf4f3c, // Round 0
            0xa0fafe17, 0x88542cb1, 0x23a33939, 0x2a6c7605, // Round 1
            0xf2c295f2, 0x7a96b943, 0x5935807a, 0x7359f67f, // Round 2
            0x3d80477d, 0x4716fe3e, 0x1e237e44, 0x6d7a883b, // Round 3
            0xef44a541, 0xa8525b7f, 0xb671253b, 0xdb0bad00, // Round 4
            0xd4d1c6f8, 0x7c839d87, 0xcaf2b8bc, 0x11f915bc, // Round 5
            0x6d88a37a, 0x110b3efd, 0xdbf98641, 0xca0093fd, // Round 6
            0x4e54f70e, 0x5f5fc9f3, 0x84a64fb2, 0x4ea6dc4f, // Round 7
            0xead27321, 0xb58dbad2, 0x312bf560, 0x7f8d292f, // Round 8
            0xac7766f3, 0x19fadc21, 0x28d12941, 0x575c006e, // Round 9
            0xd014f9a8, 0xc9ee2589, 0xe13f0cc8, 0xb6630ca6  // Round 10
        };

        pd_shared3p xor_uint32[[1]] cipherText2 = aes128EncryptEcb(expandedKey,plainText);
        if(all(declassify(cipherText1) == declassify(cipherText2))){
            succeeded_tests = succeeded_tests + 1;
            all_tests = all_tests +1;
            print("SUCCESS!");
        }
        else{
            print("FAILURE! encrypting with static expanded key invalid results. Expected: ");
            printVector(declassify(cipherText1));
            print(" but got: ");
            printVector(declassify(cipherText2));
            all_tests = all_tests +1;
        }
    }
    /*
    print("TEST 4: aes192 key generation");
    {
        for(uint i = 50; i < 300; i = i + 50){
            pd_shared3p xor_uint32[[1]] key = aes192Genkey(i);

            if(size(key) == (i * 6)){
                succeeded_tests = succeeded_tests + 1;
                all_tests = all_tests +1;
                print("SUCCESS!");
            }
            else{
                print("FAILURE! aes192 key generation failed, key too big or too small");
                all_tests = all_tests +1;
            }
        }
    }
    print("TEST 5: aes192 key expansion");
    {
        for(uint i = 2; i <= 10; i = i + 2){
            pd_shared3p xor_uint32[[1]] key = aes192Genkey(i);
            pd_shared3p xor_uint32[[1]] expandedKey = aes192ExpandKey(key);

            if(size(expandedKey) == (i * 52)){
                succeeded_tests = succeeded_tests + 1;
                all_tests = all_tests +1;
                print("SUCCESS!");
            }
            else{
                print("FAILURE! aes192 expanded key generation failed, key too big or too small");
                all_tests = all_tests +1;
            }
        }
    }
    print("TEST 6: Encrypt with aes192");
    {
        pd_shared3p xor_uint32[[1]] plainText = {0x681c7acc, 0x1cdfa764, 0x8625d98c, 0xe535075a};
        pd_shared3p xor_uint32[[1]] cipherText1 = {0xf58ba066, 0x2b2a7bcc, 0xf48583fa, 0xa27bd0e4};
        pd_shared3p xor_uint32[[1]] expandedKey = {
            0x3c3b6fb8, 0x726aa8a0, 0x1970c952, 0x751813d1, // Round 0
            0x68d6dd73, 0x635b198d, 0xbaf38a8c, 0x501f0d3f, // Round 1
            0xf871c74d, 0xb83be256, 0xca3e738d, 0xa87d149e, // Round 2
            0xf147ea32, 0x66bd8dc8, 0x1b642f8f, 0x658b4ad7, // Round 3
            0x509b7128, 0x91a40b2d, 0x3f199ff9, 0x140f7f23, // Round 4
            0xd8383c76, 0x7c1082ba, 0x3b5142df, 0x69a8251e, // Round 5
            0x9d809aee, 0xe3db62e5, 0x16c77228, 0xce86a297, // Round 6
            0xbc9189e9, 0x7869b689, 0x6adc4ca2, 0x1d081dea, // Round 7
            0xdac5af93, 0xdd1698c1, 0x39d61bfd, 0xac76771d, // Round 8
            0xcf111ab3, 0x26dc3fc,  0xbe55753f, 0x71a773ef, // Round 9
            0xa05d271c, 0x492b1739, 0x8e1c8825, 0xe3ee2a1e, // Round 10
            0xac42b230, 0xbf2e928f, 0x53e97b92, 0x7c7fca25, // Round 11
            0xecdb3c4a, 0xdb4a9382, 0x4dcaf277, 0xe9554c62  // Round 12
        };

        pd_shared3p xor_uint32[[1]] cipherText2 = aes192EncryptEcb(expandedKey,plainText);
        if(all(declassify(cipherText1) == declassify(cipherText2))){
            succeeded_tests = succeeded_tests + 1;
            all_tests = all_tests +1;
            print("SUCCESS!");
        }
        else{
            print("FAILURE! encrypting with static expanded key invalid results. Expected: ");
            printVector(declassify(cipherText1));
            print(" but got: ");
            printVector(declassify(cipherText2));
            all_tests = all_tests +1;
        }
    }
    print("TEST 7: aes256 key generation");
    {
        for(uint i = 50; i < 300; i = i + 50){
            pd_shared3p xor_uint32[[1]] key = aes256Genkey(i);

            if(size(key) == (i * 8)){
                succeeded_tests = succeeded_tests + 1;
                all_tests = all_tests +1;
                print("SUCCESS!");
            }
            else{
                print("FAILURE! aes256 key generation failed, key too big or too small");
                all_tests = all_tests +1;
            }
        }
    }
    print("TEST 8: aes256 key expansion");
    {
        for(uint i = 2; i <= 10; i = i + 2){
            pd_shared3p xor_uint32[[1]] key = aes256Genkey(i);
            pd_shared3p xor_uint32[[1]] expandedKey = aes256ExpandKey(key);

            if(size(expandedKey) == (i * 60)){
                succeeded_tests = succeeded_tests + 1;
                all_tests = all_tests +1;
                print("SUCCESS!");
            }
            else{
                print("FAILURE! aes256 expanded key generation failed, key too big or too small");
                all_tests = all_tests +1;
            }
        }
    }
    print("TEST 9: Encrypt with aes256");
    {
        pd_shared3p xor_uint32[[1]] plainText = {0x6d7134dd, 0x13026986, 0xa43e0ada, 0x569c6b53};
        pd_shared3p xor_uint32[[1]] cipherText1 = {0x91e3fd7c, 0x3221ec20, 0x64d8b896, 0xa0ccecab};
        pd_shared3p xor_uint32[[1]] expandedKey = {
            0x605a1da2, 0xd45da6a3, 0xa39eec31, 0xabe0aa21, // Round 0
            0xa20bbbf5, 0x569c6d22, 0x28e64d3d, 0xe9874a7,  // Round 1
            0x47ab59a,  0x78e61a39, 0xbfea7174, 0xa5d6dd80, // Round 2
            0xe2172627, 0x923cdd78, 0x86284505, 0xefe0327a, // Round 3
            0x5942a762, 0x198e61bd, 0x693d5105, 0x65d77c3c, // Round 4
            0xc2bf5a00, 0x50d98736, 0x8618cd8b, 0x70c4845f, // Round 5
            0x110f6080, 0x6e13b0a6, 0xba7e951c, 0x88a459a0, // Round 6
            0x588d230f, 0xc612f784, 0xfe2cd695, 0x8a18a4ba, // Round 7
            0xe22a083e, 0x9c562fe0, 0xef07711b, 0x3e42832b, // Round 8
            0x5af3d87e, 0x5ef9e2c5, 0x691c70c9, 0xbc6f3af0, // Round 9
            0x3322b501, 0x57d2dce4, 0x2e46a983, 0xbcdfe4ed, // Round 10
            0xb1aa7382, 0xc84e3060, 0x42b82e9d, 0x36b73484, // Round 11
            0x88a22615, 0x80f947ab, 0xf2fa3c0b, 0x74a313ee, // Round 12
            0xa7c7d21e, 0xacbf4bb1, 0xc531a785, 0xb599ae74, // Round 13
            0x950df596, 0x51090640, 0x71010c90, 0x506b091c  // Round 14
        };

        pd_shared3p xor_uint32[[1]] cipherText2 = aes256EncryptEcb(expandedKey,plainText);
        if(all(declassify(cipherText1) == declassify(cipherText2))){
            succeeded_tests = succeeded_tests + 1;
            all_tests = all_tests +1;
            print("SUCCESS!");
        }
        else{
            print("FAILURE! encrypting with static expanded key invalid results. Expected: ");
            printVector(declassify(cipherText1));
            print(" but got: ");
            printVector(declassify(cipherText2));
            all_tests = all_tests +1;
        }
    }
    */

    print("Test finished!");
    print("Succeeded tests: ", succeeded_tests);
    print("Failed tests: ", all_tests - succeeded_tests);

    test_report(all_tests, succeeded_tests);
}