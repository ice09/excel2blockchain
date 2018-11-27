pragma solidity ^0.4.23;

contract ExcelStorage {

    mapping (uint8 => mapping (string => string)) excelStorage;
    event Stored(uint8 _index, string _key, string _value);

    function put(uint8 _index, string _key, string _value) public {
        excelStorage[_index][_key] = _value;
        emit Stored(_index, _key, _value);
    }

    function get(uint8 _index, string _key) view public returns (string) {
        return excelStorage[_index][_key];
    }

}
