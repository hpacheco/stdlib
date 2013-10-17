/*
 * This file is a part of the Sharemind framework.
 * Copyright (C) Cybernetica AS
 *
 * All rights are reserved. Reproduction in whole or part is prohibited
 * without the written consent of the copyright owner. The usage of this
 * code is subject to the appropriate license agreement.
 */

/**
 * \cond
 */
module a3p_statistics_common;

import a3p_random;
import additive3pp;
import stdlib;
/**
 * \endcond
 */


/**
 * @file
 * \defgroup a3p_statistics_common a3p_statistics_common.sc
 * \defgroup cut cut
 * \defgroup cut_n cut(multiple samples)
 * \defgroup nth_element nthElement
 * \defgroup nth_element_indexes nthElement(with indexes)
 * \defgroup contingency contingencyTable
 */

/** \addtogroup <a3p_statistics_common>
 *  @{
 *  @brief Module with statistics support functions that are useful
 *  primarily in other statistics modules.
 */

/**
 * \cond
 */
template <domain D : additive3pp, type T>
D T[[1]] _cut (D T[[1]] data, D bool[[1]] isAvailable){
	// Shuffle, open isAvailable, remove all where isAvailable = 0

	assert (size (data) == size (isAvailable));

	uint s = size (data);

	D T[[2]] matrix (s, 2), shufMat (s, 2);
	matrix[:,0] = data;
	matrix[:,1] = (T)isAvailable;

	shufMat = shuffleRows (matrix);

	T[[1]] shufIsAvailablePub (s);
	shufIsAvailablePub = declassify (shufMat[:, 1]);

    T countAvailable = 0;

    countAvailable = sum (shufIsAvailablePub);
	/*for (uint i = 0; i < s; i = i + 1){
		countAvailable = countAvailable + shufIsAvailablePub[i];
	}*/

	D T[[1]] cutData ((uint)countAvailable);
	uint indexCutData = 0;

	for (uint i = 0; i < s; i = i + 1){
		if (shufIsAvailablePub [i] == 1){
			cutData [indexCutData] = shufMat [i, 0];
			indexCutData = indexCutData + 1;
		}
	}

	return cutData;
}
/**
 * \endcond
 */

/** \addtogroup <cut>
 *  @{
 *  @brief Remove unavailable elements
 *  @note **D** - additive3pp protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64"
 *  @param data - input vector
 *  @param isAvailable - vector indicating which elements of the input vector are available
 *  @return returns a vector where elements of the input vector have
 *   been removed if the corresponding element in isAvailable is zero.
 */
template <domain D : additive3pp>
D int32[[1]] cut (D int32[[1]] data, D bool[[1]] isAvailable) {
    return _cut (data, isAvailable);
}

template <domain D : additive3pp>
D int64[[1]] cut (D int64[[1]] data, D bool[[1]] isAvailable) {
    return _cut (data, isAvailable);
}
/**
 * @}
 */

/**
 * \cond
 */
// This cutting function is used by Wilcoxon tests
template <domain D : additive3pp, type T>
D T[[2]] _cut (D T[[2]] data, D bool[[1]] isAvailable) {
	uint64[[1]] shapeData = shape (data);

	// Shuffle, open isAvailable, remove all where isAvailable = 0
	assert (shapeData[0] == size (isAvailable));

	uint rows = shapeData[0];
	uint columns = shapeData[1];

	D T[[2]] matrix (rows, columns + 1), shufMat (rows, columns + 1);
	matrix[:, 0 : columns] = data;
	matrix[:, columns] = (T)isAvailable;

	shufMat = shuffleRows (matrix);

	T[[1]] shufIsAvailablePub (rows);
	shufIsAvailablePub = declassify (shufMat[:, columns]);

	T countAvailable = 0;

	for (uint i = 0; i < rows; i = i + 1){
		countAvailable = countAvailable + shufIsAvailablePub[i];
	}

	D T[[2]] cutData ((uint64)countAvailable, columns);
	uint indexCutData = 0;

	for (uint i = 0; i < rows; i = i + 1){
		if (shufIsAvailablePub [i] != 0){
			cutData [indexCutData, :] = shufMat [i, 0 : columns];
			indexCutData = indexCutData + 1;
		}
	}

	return cutData;
}
/**
 * \endcond
 */

/** \addtogroup <cut_n>
 *  @{
 *  @brief Remove unavailable elements from N samples using the same
 *  shuffling key
 *  @note **D** - additive3pp protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64"
 *  @param data - input matrix. Each column is a sample.
 *  @param isAvailable - vector indicating which elements of the input
 *  samples are available. Has to have as many elements as there are
 *  rows in the data matrix.
 */
template <domain D : additive3pp>
D int32[[2]] cut (D int32[[2]] data, D bool[[1]] isAvailable) {
    return _cut (data, isAvailable);
}

template <domain D : additive3pp>
D int64[[2]] cut (D int64[[2]] data, D bool[[1]] isAvailable) {
    return _cut (data, isAvailable);
}
/** @} */


/**
 * \cond
 */
// Partition the data
// Source: http://en.wikipedia.org/wiki/Selection_algorithm#Partition-based_general_selection_algorithm
template <domain D, type T>
D T[[1]] _partition(D T[[1]] dataVector, uint left, uint right, uint pivotIndex) {
    D T pivotValue = dataVector[pivotIndex];
    dataVector[pivotIndex] = dataVector[right];
    dataVector[right] = pivotValue;
    uint storeIndex = left;
    D T temp;
    for (uint i = left; i < right; i++) {
        // NB! We leak a quite random comparison result
        if (declassify (dataVector[i] < pivotValue)) {
            temp = dataVector[storeIndex];
            dataVector[storeIndex] = dataVector[i];
            dataVector[i] = temp;
            storeIndex++;
        }
    }
    temp = dataVector[storeIndex];
    dataVector[storeIndex] = dataVector[right];
    dataVector[right] = temp;

    D T[[1]] retval (size(dataVector) + 1);
    retval[0] = (T)storeIndex;
    retval[1:size(dataVector)+1] = dataVector[0:size(dataVector)];
    return retval;
}

template <domain D : additive3pp, type T>
D T _nthElement (D T[[1]] data, uint64 left, uint64 right, uint64 k, bool shuffle) {
    assert(left >= 0);
    assert(right < size(data));
    assert(k < size(data));

    // The algorithm uses k = 1... internally
    k = k + 1;

    // IMPORTANT! We need to shuffle, because we do not hide the execution flow
    // Sometimes the data is already shuffled (like with shuffling and cutting)
    // Then reshuffling is unnecessary
    if (shuffle){
    	data = shuffle (data);
	}

    while (true) {
        uint pivotIndex = (left + right) / 2;

        // TODO: We need either structures or references to let
        // partition return both the new data and the index.
        // The current hack of putting the index into the vector is
        // quite bad.
        D T[[1]] partData = _partition (data, left, right, pivotIndex);
        uint pivotNewIndex = (uint)declassify (partData[0]);
        data[0:size(data)] = partData[1:size(data)+1];

        uint pivotDist = pivotNewIndex - left + 1;

        // NB! We leak a random? comparison result
        if (pivotDist == k) {
            return data[pivotNewIndex];

        } else if (k < pivotDist) {
            right = pivotNewIndex - 1;

        } else {
            k = k - pivotDist;
            left = pivotNewIndex + 1;
        }
    }
    return 0;
}
/**
 * \endcond
 */


/** \addtogroup <nth_element>
 *  @{
 *  @brief Find the nth element in size from a vector
 *  @note **D** - additive3pp protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64"
 *  @param data - input vector
 *  @param k - index of the value's rank (starts from 0)
 *  @param shuffle - indicates if the input vector should be shuffled
 *  before running the algorithm. Shuffling is necessary to hide the
 *  execution flow but if the input vector has already been shuffled
 *  it's unnecessary.
 *  @return returns the nth element in size of the input vector.
 */
template<domain D : additive3pp>
D int32 nthElement (D int32[[1]] data, uint64 k, bool shuffle) {
    return nthElement (data, 0::uint, size(data)-1, k, shuffle);
}

template<domain D : additive3pp>
D int64 nthElement (D int64[[1]] data, uint64 k, bool shuffle) {
    return nthElement (data, 0::uint, size(data)-1, k, shuffle);
}
/** @} */


/** \addtogroup <nth_element_indexes>
 *  @{
 *  @brief Find the nth element in size from a vector
 *  @note **D** - additive3pp protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64"
 *  @param data - input vector
 *  @param left - index of the start of range to be searched (inclusive)
 *  @param right - index of the end of range to be searched (inclusive)
 *  @param k - index of the value's rank (starts from 0)
 *  @param shuffle - indicates if the input vector should be shuffled
 *  before running the algorithm. Shuffling is necessary to hide the
 *  execution flow but if the input vector has already been shuffled
 *  it's unnecessary.
 *  @return returns the nth element in size of the input vector.
 */
template<domain D : additive3pp>
D int32 nthElement (D int32[[1]] data, uint64 left, uint64 right, uint64 k, bool shuffle) {
    return _nthElement (data, left, right, k, shuffle);
}

template<domain D : additive3pp>
D int64 nthElement (D int64[[1]] data, uint64 left, uint64 right, uint64 k, bool shuffle) {
    return _nthElement (data, left, right, k, shuffle);
}
/** @} */


/** \cond */
template <domain D, type T>
D T[[2]] _contingencyTable (D T[[1]] data, D bool[[1]] cases, D bool[[1]] controls, T[[2]] codeBook) {

    assert (size (data) == size (cases) && size (data) == size (controls));

	// Count the classes and check whether the codeBook contains zeroes
	uint[[1]] shapeCodeBook = shape (codeBook);
	uint codeCount = shapeCodeBook[1];
	uint dataSize = size (data);

	bool codeBookZeroes = false;
	T classCount = 1;
	for (uint i = 0; i < codeCount; i = i + 1) {
		if (codeBook [0, i] == 0){
			// codeBook contains zeroes, must be dealt with separately
			codeBookZeroes = true;
		}
		if (classCount < codeBook[1, i]) {
			classCount = codeBook[1, i];
		}
	}

	// If the codebook contains zeroes, add the value of the filter to every element
	// This will increase the codes by 1 where the filter contains 1 and will not change the value otherwise

	D T[[1]] dataCases = data;
    D T[[1]] dataControls = data;

	if (codeBookZeroes) {
		for (uint i = 0; i < dataSize; i = i + 1) {
			dataCases = dataCases + (T)cases;
			dataControls = dataControls + (T)controls;
		}
		codeBook [0, :] = codeBook [0, :] + 1;
	}

	dataCases = dataCases * (T)cases;
	dataControls = dataControls * (T)controls;

	D T[[1]] parEqA (codeCount * dataSize * 2), parEqB (codeCount * dataSize * 2), parEqRes (codeCount * dataSize * 2);

    for (uint i = 0; i < codeCount; i = i + 1) {
		parEqA[i * 2 * dataSize : (i * 2 + 1) * dataSize] = dataCases;
		parEqA[(i * 2 + 1) * dataSize : (i + 1) * 2 * dataSize] = dataControls;
		parEqB[i * 2 * dataSize : (i + 1) * 2 * dataSize] = (T)codeBook[0, i];
	}

	parEqRes = (T) (parEqA == parEqB);
    D T[[1]] parEqSums (codeCount * 2);
    parEqSums = sum (parEqRes, codeCount * 2);

	D T[[2]] contTable ((uint) classCount, 2) = 0;
    for (uint i = 0; i < codeCount; i = i + 1) {
       	uint64 class = (uint) codeBook[1, i] - 1;

		contTable[class, 0] = contTable[class, 0] + (T)parEqSums[i * 2];
		contTable[class, 1] = contTable[class, 1] + (T)parEqSums[i * 2 + 1];
	}

	return contTable;
}
/** \endcond */


/** \addtogroup <contingency>
 *  @{
 *  @brief Create a contingency table
 *  @note **D** - all protection domains
 *  @note Supported types - \ref uint32 "uint32" / \ref uint64 "uint64"
 *  @param data - input vector
 *  @param cases - vector indicating which elements of data belong to cases
 *  @param controls - vector indicating which elements of data belong to controls
 *  @param codeBook - matrix used for coding the answer. The first row
 *  contains expected values of the input vector and the second row
 *  contains the classes that these values will be put into. The
 *  classes should begin with 1.
 *  @return returns a contingency table in the format
 *  @return
 *  <table>
 *  <tr><td></td><td>Cases</td><td>Controls</td></tr>
 *  <tr><td>Option 1</td><td>c1</td><td>d1</td></tr>
 *  <tr><td>Option 2</td><td>c2</td><td>d2</td></tr>
 *  <tr><td>Option 3</td><td>c3</td><td>d3</td></tr>
 *  <tr><td>…</td><td>…</td><td>…</td></tr>
 *  </table>
 */
template<domain D>
D uint32[[2]] contingencyTable (D uint32[[1]] data,
                                D bool[[1]] cases,
                                D bool[[1]] controls,
                                uint32[[2]] codeBook) {

    return _contingencyTable (data, cases, controls, codeBook);
}

template<domain D>
D uint64[[2]] contingencyTable (D uint64[[1]] data,
                                D bool[[1]] cases,
                                D bool[[1]] controls,
                                uint64[[2]] codeBook) {

    return _contingencyTable (data, cases, controls, codeBook);
}
/**
 * @}
 */
/**
 * @}
 */