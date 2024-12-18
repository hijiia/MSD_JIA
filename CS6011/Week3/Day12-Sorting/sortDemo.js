function compareFunc (a , b){
        return a < b;
}

function findMinIndex (arr, start, compareFunc) {
    let minIndex = start;
    for (let i = start + 1; i< arr.length; i++){
        if (compareFunc(arr[minIndex], arr[i])){
            minIndex = i;
        }
    }
    return minIndex;
}

function Sorting (arr, compareFunc){
    let minIndex = findMinIndex();
    for (let i = 0; i < arr.length-1; i++){
        if (minIndex !== i){
            let temp = arr[i];
            arr[i] = arr[minIndex];
            arr[minIndex] = temp;
        }
    }
    return arr;
}