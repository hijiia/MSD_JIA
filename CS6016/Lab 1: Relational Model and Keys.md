# Lab 1: Relational Model and Keys  
### Part 1 - English to Schema
```
Table Name [key (type), attribute1 (type), attribute2 (type), ...]
```
- A student has a student ID, a name, and a GPA.
```
Student [__sID (integer)__, name (string), GPA (real)]
```
- A grocery store needs to track an inventory of products for sale. It has zero or more of each type of product for sale, and needs to track the quantity and price for each product. A product has a name and a "stock keeping unit" (SKU) (hint: this is a real thing, you may google it). Remember that a valid table instance can't have duplicate rows - the store does not care about differentiating between individual items of the same product type, but it does want to be able to count them.
```
Inventory[_SKU(string)_, productName(string), quantity(integer), price(real)]
```
- Consider the grocery store database from the previous problem, but with a few differences: Now we don't care about tracking quantity, but we do want to track which aisle(s) the product is to be displayed on. Sometimes a product is displayed on more than one aisle in special display racks, but the product can not have multiple display cases per aisle. You may copy the relevant parts from your previous answer, but they will need modifications/additions.
```
Inventory[_SKU(string)_, productName(string), price(real)]
Location[_SKU(string)_, _Asisle(integer)_]
```
- A car has a make, model, year, color, and VIN (vehicle identification number). A salesperson has a name and a social security number, and is responsible for trying to sell zero or more cars. A car dealership has an inventory of cars and a set of salespeople. It needs to keep track of which car(s) each salesperson is trying to sell. More than one salesperson can be assigned to any given car, but a car does not necessarily have any salespeople assigned to it.
```
M-M
Car[VIN (string), make (string), model (string)
year (integer), color (string)]
Salesperson[SSN (string), name (string)]
Sell [VIN (string), SSN (string)]
```
### Part 2 - SQL Table Declarations

```
CREATE TABLE Patrons (
  Name string,
  CardNum integer,
  PRIMARY KEY (CardNum)
);

CREATE TABLE Inventory (
  Serial integer,
  ISBN string,
  PRIMARY KEY (Serial)
);

CREATE TABLE Titles (
  ISBN string,
  Title string,
  Author string,
  PRIMARY KEY (ISBN)
);

CREATE TABLE CheckedOut (
  CardNum integer,
  Serial integer,
  PRIMARY KEY (CardNum, Serial)
);

CREATE TABLE Phones (
  CardNum integer,
  Phone string,
  PRIMARY KEY (CardNum, Phone)
);
```

### Part 3 - Fill in Tables
Car
VIN               | make    | model    | year | color
-------------------|---------|----------|------|-------
11111111111111111 | Toyota  | Tacoma   | 2008 | Red
22222222222222222 | Toyota  | Tacoma   | 1999 | Green
33333333333333333 | Tesla   | Model 3  | 2018 | White
44444444444444444 | Subaru  | WRX      | 2016 | Blue
55555555555555555 | Ford    | F150     | 2004 | Red

Salesperson
SSN         | name
------------|--------
123-45-6789 | Arnold
987-65-4321 | Hannah
555-12-3456 | Steve

Sell
VIN                | SSN
-------------------|------------
11111111111111111 | 123-45-6789 | -- Arnold red Toyota Tacoma
22222222222222222  | 123-45-6789 | -- Arnold green Toyota Tacoma
33333333333333333 | 555-12-3456  |-- Steve Tesla
11111111111111111 | 987-65-4321  |-- Hannah red Toyota Tacoma
55555555555555555 | 987-65-4321  |-- Hannahred Ford F150


### Part 4 - Keys and Superkeys

| Attribute Sets | Superkey? | Proper Subsets | Key? |
|----------------|-----------|----------------|------|
| {A1}           | No        | {}             | No   |
| {A2}           | No        | {}             | No   |
| {A3}           | No        | {}             | No   |
| {A1, A2}       | Yes       | {A1}, {A2}     | Yes  |
| {A1, A3}       | No        | {A1}, {A3}     | No   |
| {A2, A3}       | No        | {A2}, {A3}     | No   |
| {A1, A2, A3}   | Yes       | {A1}, {A2}, {A3}, {A1,A2}, {A1,A3}, {A2,A3} | No |

### Part 5 - Abstract Reasoning
- If {x} is a superkey, then any set containing x is also a superkey.  

*true, if one {x} can identify all tuples, then any larger sets containing x is also uniquely identified*

- If {x} is a key, then any set containing x is also a key.

*false, any set containing x could be a superkey,key is the minimum superkey*

- If {x} is a key, then {x} is also a superkey.

*true, if one want to be a key, it has to be a superkey first*

- If {x, y, z} is a superkey, then one of {x}, {y}, or {z} must also be a superkey.

*false, sometime a superkey need multiple attributes work together so that can identified uniquely, being a superkey individually is not a must*

- If an entire schema consists of the set {x, y, z}, and if none of the proper subsets of {x, y, z} are keys, then {x, y, z} must be a key.

*true, if no proper subset is a key, then no proper subset can uniquely identify all tuples. Since the entire schema {x, y, z} must contain at least one key (every relation has at least one key), and no smaller subset works, then {x, y, z} itself must be the key.*

