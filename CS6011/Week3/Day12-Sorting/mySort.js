function compareFunc (a,b){
    return a < b
}

//find the index of the minimum element
function findMinLocation(arr, start, compareFunc) {
    let minIndex = start;
    for (let i = start + 1; i < arr.length; i++) {
        if (compareFunc(arr[i], arr[minIndex])) {
            minIndex = i;
        }
    }
    return minIndex;
}

// Selection sort
function selectionSort(arr, compareFunc) {
    for (let i = 0; i < arr.length - 1; i++) {
        let minIndex = findMinLocation(arr, i, compareFunc);
        if (minIndex !== i) {
            // Swap the elements
            let temp = arr[i];
            arr[i] = arr[minIndex];
            arr[minIndex] = temp;
        }
    }
    return arr;
}

// Sorting integers
let intArray = [5, 3, 8, 1, 2];
console.log("Original int array:", intArray);
selectionSort(intArray, compareFunc);
console.log("Sorted int array:", intArray);

// Sorting floating point numbers
let floatArray = [2.5, 0.1, 3.3, 1.7, 4.0];
console.log("Original float array:", floatArray);
selectionSort(floatArray, compareFunc);
console.log("Sorted float array:", floatArray);

// Sorting strings
let stringArray = ["apple", "google", "bytedance", "tencent"];
console.log("Original string array:", stringArray);
selectionSort(stringArray, (a, b) => a.toLowerCase() < b.toLowerCase());
console.log("Sorted string array:", stringArray);

// Sorting mixed array
let mixedArray = [5, "apple", 2.1, "Banana", 8];
console.log("Original mixed array:", mixedArray);
selectionSort(mixedArray, (a, b) => String(a).toLowerCase() < String(b).toLowerCase());
console.log("Sorted mixed array:", mixedArray);

// person object
let people = [
    { first: "Jia", last: "Gao" },
    { first: "Chloe", last: "Andrew" },
    { first: "Alice", last: "Tao" },
    { first: "Nick", last: "Nelson" }
];

// sorting by last name, and then by first name
function compareByLastName(a, b) {
    if (a.last.toLowerCase() === b.last.toLowerCase()) {
        return a.first.toLowerCase() < b.first.toLowerCase();
    }
    return a.last.toLowerCase() < b.last.toLowerCase();
}

// sorting by first name, and then by last name
function compareByFirstName(a, b) {
    if (a.first.toLowerCase() === b.first.toLowerCase()) {
        return a.last.toLowerCase() < b.last.toLowerCase();
    }
    return a.first.toLowerCase() < b.first.toLowerCase();
}

// Sort by last name
console.log("People sorted by last name:");
selectionSort(people, compareByLastName);
console.log(people);

// Sort by first name
console.log("People sorted by first name:");
selectionSort(people, compareByFirstName);
console.log(people);