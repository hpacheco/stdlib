/*
 * This file is a part of the Sharemind framework.
 * Copyright (C) Cybernetica AS
 *
 * All rights are reserved. Reproduction in whole or part is prohibited
 * without the written consent of the copyright owner. The usage of this
 * code is subject to the appropriate license agreement.
 */

/** \cond */
module table_database;
/** \endcond */

/**
 * @file
 * \defgroup table_database table_database.sc
 * \defgroup tdb_vmap_new tdbVmapNew
 * \defgroup tdb_vmap_delete tdbVmapDelete
 * \defgroup tdb_vmap_count tdbVmapCount
 * \defgroup tdb_vmap_erase tdbVmapErase
 * \defgroup tdb_vmap_add_string tdbVmapAddString
 * \defgroup tdb_vmap_clear tdbVmapClear
 * \defgroup tdb_vmap_reset tdbVmapReset
 * \defgroup tdb_vmap_add_batch tdbVmapAddBatch
 * \defgroup tdb_vmap_set_batch tdbVmapSetBatch
 * \defgroup tdb_vmap_get_batch_count tdbVmapGetBatchCount
 * \defgroup tdb_open_connection tdbOpenConnection
 * \defgroup tdb_close_connection tdbCloseConnection
 * \defgroup tdb_table_create tdbTableCreate
 * \defgroup tdb_table_delete tdbTableDelete
 * \defgroup tdb_table_exists tdbTableExists
 * \defgroup tdb_insert_row tdbInsertRow
 * \defgroup tdb_get_row_count tdbGetRowCount
 * \defgroup tdb_vmap_value_vector_size tdbVmapValueVectorSize
 * \defgroup tdb_read_column tdbReadColumn
 * \defgroup tdb_get_value_string tdbVmapGetValue(string)
 */

/** \addtogroup <table_database>
 *  @{
 *  @brief Module for working with table databases.
 */

/** \addtogroup <tdb_vmap_new>
 *  @{
 *  @brief Create a vector map
 *  @return returns an identifier that can be used for working with
 *  the vector map
 */
uint64 tdbVmapNew () {
    uint64 rv = 0;
    __syscall ("tdb_vmap_new", __return rv);
    return rv;
}
/** @} */

/** \addtogroup <tdb_vmap_delete>
 *  @{
 *  @brief Delete a vector map
 *  @param id - vector map id
 */
void tdbVmapDelete (uint64 id) {
    __syscall ("tdb_vmap_delete", id);
}
/** @} */

/** \addtogroup <tdb_vmap_count>
 *  @{
 *  @brief Get the number of vectors in a vector map
 *  @param id - vector map id
 *  @param paramname - name of the vector to count
 *  @return returns the number of vectors with the name paramname in the vector map (one or zero)
 */
uint64 tdbVmapCount (uint64 id, string paramname) {
    uint64 rv = 0;
    __syscall ("tdb_vmap_count", id, __cref paramname, __return rv);
    return rv;
}
/** @} */

/** \addtogroup <tdb_vmap_value_vector_size>
 *  @{
 *  @brief Get the size of a vector in a vector map
 *  @param id - vector map id
 *  @param paramname - name of the vector in the vector map
 *  @return returns the number of values in the vector
 */
uint64 tdbVmapValueVectorSize(uint64 id, string paramname) {
    uint64 rsize = 0;
    __syscall("tdb_vmap_size_value", id, __cref paramname, __return rsize);
    return rsize;
}
/** @} */

/** \addtogroup <tdb_vmap_erase>
 *  @{
 *  @brief Remove a vector from a vector map
 *  @param id - vector map id
 *  @param paramname - name of the removed vector
 *  @return returns true if a vector was removed, false otherwise
 */
bool tdbVmapErase (uint64 id, string paramname) {
    uint64 rv = 0;
    __syscall ("tdb_vmap_erase", id, __cref paramname, __return rv);
    return rv != 0;
}
/** @} */

/** \addtogroup <tdb_vmap_add_string>
 *  @{
 *  @brief Add a string to a vector in a vector map
 *  @param id - vector map id
 *  @param paramname - name of the vector to which the string is added
 *  @param str - string to be added
 */
void tdbVmapAddString (uint64 id, string paramname, string str) {
    __syscall ("tdb_vmap_push_back_string", id, __cref paramname, __cref str);
}
/** @} */

/** \addtogroup <tdb_vmap_clear>
 *  @{
 *  @brief Clears the current batch in a vector map
 *  @param id - vector map id
 */
void tdbVmapClear (uint64 id) {
    __syscall ("tdb_vmap_clear", id);
}
/** @} */

/** \addtogroup <tdb_vmap_reset>
 *  @{
 *  @brief Resets the vector map to the initial state
 *  @param id - vector map id
 */
void tdbVmapReset (uint64 id) {
    __syscall ("tdb_vmap_reset", id);
}
/** @} */

/** \addtogroup <tdb_vmap_add_batch>
 *  @{
 *  @brief Add a batch to a vector map.
 *  @note A vector map always contains at least one batch. Batches are
 *  used for combining multiple database operations. For instance, to
 *  insert two rows at once, add values of the first row to a vector
 *  map, call this function, add values of the second row and call
 *  tdbInsertRow.
 *  @param id - vector map id
 */
void tdbVmapAddBatch (uint64 id) {
    __syscall ("tdb_vmap_add_batch", id);
}
/** @} */

/** \addtogroup <tdb_vmap_set_batch>
 *  @{
 *  @brief Sets the current active batch
 *  @note Useful for iterating through the batches of the result returned by a
 *  database operation.
 *  @param id - vector map id
 *  @param index - index of the batch in the vector map
 */
void tdbVmapSetBatch (uint64 id, uint64 index) {
    __syscall ("tdb_vmap_set_batch", id, index);
}
/** @} */

/** \addtogroup <tdb_vmap_get_batch_count>
 *  @{
 *  @brief Get number of batches in a vector map
 *  @param id - vector map id
 *  @return returns the number of batches in the vector map
 */
uint64 tdbVmapGetBatchCount (uint64 id) {
    uint64 out;
    __syscall ("tdb_vmap_batch_count", id, __return out);
    return out;
}
/** @} */

/** \addtogroup <tdb_open_connection>
 *  @{
 *  @brief Open connection to a data source
 *  @param datasource - data source name
 */
void tdbOpenConnection (string datasource) {
    __syscall ("tdb_open", __cref datasource);
}
/** @} */

/** \addtogroup <tdb_close_connection>
 *  @{
 *  @brief Close connection to a data source
 *  @param datasource - data source name
 */
void tdbCloseConnection (string datasource) {
    __syscall ("tdb_close", __cref datasource);
}
/** @} */

/** \addtogroup <tdb_table_create>
 *  @{
 *  @brief Create a table
 *  @param datasource - name of the data source that will contain the table
 *  @param table - table name
 *  @param parameters - id of the vector map containing parameters for
 *  creating the table. The vectors "types" and "names" should contain
 *  the types and names of the columns.
 */
void tdbTableCreate (string datasource, string table, uint64 parameters) {
    __syscall ("tdb_stmt_exec", __cref datasource, __cref table, __cref "tbl_create", parameters);
}
/** @} */

/** \addtogroup <tdb_table_delete>
 *  @{
 *  @brief Delete a table
 *  @param datasource - name of the data source containing the table
 *  @param table - table name
 */
void tdbTableDelete (string datasource, string table) {
    __syscall ("tdb_tbl_delete", __cref datasource, __cref table);
}
/** @} */

/** \addtogroup <tdb_table_exists>
 *  @{
 *  @brief Check if a table exists
 *  @param datasource - name of the data source that is expected to contain the table
 *  @param table - table name
 *  @return returns true if the table exists
 */
bool tdbTableExists (string datasource, string table) {
    uint64 rv = 0;
    __syscall ("tdb_tbl_exists", __cref datasource, __cref table, __return rv);
    return rv != 0;
}
/** @} */

/** \addtogroup <tdb_insert_row>
 *  @{
 *  @brief Insert a row into a table
 *  @param datasource - name of the data source containing the table
 *  @param table - table name
 *  @param parameters - id of the vector map containing values to be
 *  inserted. The vector "values" should contain a value for each
 *  column.
 */
void tdbInsertRow (string datasource, string table, uint64 parameters) {
    __syscall ("tdb_stmt_exec", __cref datasource, __cref table, __cref "insert_row", parameters);
}
/** @} */

/** \addtogroup <tdb_get_row_count>
 *  @{
 *  @brief Get the number of rows in a table
 *  @param datasource - name of the data source containing the table
 *  @param table - table name
 *  @return returns the number of rows in the table
 */
uint64 tdbGetRowCount (string datasource, string table) {
    uint64 count = 0;
    __syscall ("tdb_tbl_row_count", __cref datasource, __cref table, __return count);
    return count;
}
/** @} */

/** \addtogroup <tdb_read_column>
 *  @{
 *  @brief Read a column from a table
 *  @param datasource - name of the data source containing the table
 *  @param table - table name
 *  @param index - index of the column in the table
 *  @return Returns a vector map id. The first value of the "values"
 *  vector in the vector map contains the values in the column.
 */
uint64 tdbReadColumn(string datasource, string table, uint64 index) {
    uint64 rv = 0;
    __syscall("tdb_read_col", __cref datasource, __cref table, index, __return rv);
    return rv;
}
/** @} */

/** \addtogroup <tdb_get_value_string>
 *  @{
 *  @brief Read a string from a vector in a vector map
 *  @param id - id of the vector map
 *  @param paramname - name of the vector
 *  @param index - index of the element in the vector
 *  @return returns the string in the vector at the specified index
 */
string tdbVmapGetValue(uint64 id, string paramname, uint64 index) {
    uint64 isvec = 0;
    __syscall("tdb_vmap_is_value_vector", id, __cref paramname, __return isvec);
    assert(isvec != 0);

    uint64 vsize = 0;
    __syscall("tdb_vmap_size_value", id, __cref paramname, __return vsize);
    assert(index < vsize);

    string rt_name;
    __syscall("tdb_vmap_at_value_type_name", id, __cref paramname, index, __return rt_name);
    assert(rt_name == "string");

    string rt_dom;
    __syscall("tdb_vmap_at_value_type_domain", id, __cref paramname, index, __return rt_dom);
    assert(rt_dom == "public");

    uint64 rt_num_bytes = 0;
    __syscall("tdb_vmap_at_value", id, __cref paramname, index, __return rt_num_bytes);

    uint8[[1]] rt_bytes(rt_num_bytes);
    __syscall("tdb_vmap_at_value", id, __cref paramname, index, __ref rt_bytes);

    return __string_from_bytes(rt_bytes[:rt_num_bytes]); // exclude null-terminator byte
}
/** @} */

/** @} */
