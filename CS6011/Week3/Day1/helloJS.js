document.body.innerHTML += '<p>Hello World</p>';
console.log('Hello World');

let myArray = ["Jia", true, 88, 3.14, {name: "Javascript"}];
console.log(myArray);
myArray[1] = false;
myArray.push("New Element");
console.log("Modified array:", myArray);
function add(a, b) {
    return a + b;
}

let addFunction = function(a, b) {
    return a + b;
};

console.log(add(5, 3));
console.log(addFunction(5, 4.5));
console.log(add("Hello ", "World")); 
console.log(addFunction("Hello", "World"));
