/*
 * This file is a part of the Sharemind framework.
 * Copyright (C) Cybernetica AS
 *
 * All rights are reserved. Reproduction in whole or part is prohibited
 * without the written consent of the copyright owner. The usage of this
 * code is subject to the appropriate license agreement.
 */

module float_precision;

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


/****************************************************
*****************************************************
*****************************************************
*****/      public uint test_amount = 10;       /****
******  increase for more accurate percentages  *****
*****************************************************
*****************************************************
*****************************************************/


domain pd_shared3p shared3p;

template<type T>
T random_float(T data){
    T rand = 1;
    pd_shared3p uint32 temp;
    pd_shared3p int8 temp2;
    T scalar;
    T scalar2;
    for(uint i = 0; i < 2; ++i){
        scalar = 0;
        while(scalar == 0 || scalar2 == 0){
            scalar = (T) declassify(randomize(temp));
            scalar2 = (T) declassify(randomize(temp2));
        }
        if((i % 2) == 0){
            rand *= scalar;
            rand *= scalar2;
        }
        else{
            rand /= scalar;
            rand /= scalar2;
        }
    }
    return rand;
}

template<domain D:shared3p,type T>
D T[[1]] random(D T[[1]] data){
    uint x_shape = size(data);
    T[[1]] rand (x_shape) = 1;
    pd_shared3p uint32[[1]] temp (x_shape);
    pd_shared3p int8[[1]] temp2 (x_shape);
    T[[1]] scalar (x_shape);
    T[[1]] scalar2 (x_shape);
    for(uint i = 0; i < 2; ++i){
        scalar[0] = 0;
        while(any(scalar == 0) || any(scalar2 == 0)){
            scalar = (T) declassify(randomize(temp));
            scalar2 = (T) declassify(randomize(temp2));
        }
        if((i % 2) == 0){
            rand *= scalar;
            rand *= scalar2;
        }
        else{
            rand /= scalar;
            rand /= scalar2;
        }
    }
    pd_shared3p T[[1]] result = rand;
    return result;
}

template<domain D:shared3p,type T>
D T[[2]] random(D T[[2]] data){
    uint x_shape = shape(data)[0];
    uint y_shape = shape(data)[1];
    T[[2]] rand (x_shape,y_shape) = 1;
    pd_shared3p uint32[[2]] temp (x_shape,y_shape);
    pd_shared3p int8[[2]] temp2 (x_shape,y_shape);
    T[[2]] scalar (x_shape,y_shape);
    T[[2]] scalar2 (x_shape,y_shape);
    for(uint i = 0; i < 2; ++i){
        scalar[0,0] = 0;
        while(any(scalar == 0) || any(scalar2 == 0)){
            scalar = (T) declassify(randomize(temp));
            scalar2 = (T) declassify(randomize(temp2));
        }
        if((i % 2) == 0){
            rand *= scalar;
            rand *= scalar2;
        }
        else{
            rand /= scalar;
            rand /= scalar2;
        }
    }
    pd_shared3p T[[2]] result = rand;
    return result;
}

template<domain D:shared3p,type T>
D T[[3]] random(D T[[3]] data){
    uint x_shape = shape(data)[0];
    uint y_shape = shape(data)[1];
    uint z_shape = shape(data)[2];
    T[[3]] rand (x_shape,y_shape,z_shape) = 1;
    pd_shared3p uint32[[3]] temp (x_shape,y_shape,z_shape);
    pd_shared3p int8[[3]] temp2 (x_shape,y_shape,z_shape);
    T[[3]] scalar (x_shape,y_shape,z_shape);
    T[[3]] scalar2 (x_shape,y_shape,z_shape);
    for(uint i = 0; i < 2; ++i){
        scalar[0,0,0] = 0;
        while(any(scalar == 0) || any(scalar2 == 0)){
            scalar = (T) declassify(randomize(temp));
            scalar2 = (T) declassify(randomize(temp2));
        }
        if((i % 2) == 0){
            rand *= scalar;
            rand *= scalar2;
        }
        else{
            rand /= scalar;
            rand /= scalar2;
        }
    }
    pd_shared3p T[[3]] result = rand;
    return result;
}

template<type T>
void test_multiplication(T data){
    T max_absolute = 0, max_relative = 0;
    T temp; T temp2;
    pd_shared3p T a;
    pd_shared3p T b;
    T sum; T sum2;
    for(uint i = 0; i < test_amount; ++i){
        print("test: ",i+1);
        a = classify(random_float(0::T)); b = classify(random_float(0::T));
        sum = declassify(a*b); sum2 = declassify(a) * declassify(b);
        temp = sum-sum2; temp2 = sum2;
        if(temp < 0){temp = -temp};
        if(temp2 < 0){temp2 = -temp2};
        if (max_absolute < temp)
            max_absolute = temp;
        if (max_relative < temp / temp2)
            max_relative = temp / temp2;
    }

    print("TEST completed");
    print("Max absolute error: ", max_absolute);
    print("Max relative error: ", max_relative);
    test_report_error(max_relative);
}

void main(){

    print("Precision test: start");

    print("TEST 3: Float32/64 Multiplication precision");
    {
        print("Float32");
        test_multiplication(0::float32);
    }
    {
        print("Float64");
        test_multiplication(0::float64);
    }

    print("Test finished!");
}