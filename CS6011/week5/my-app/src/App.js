
import './App.css';
import { useState } from 'react';
import ToDoList from './ToDoList';
import InputArea from './InputArea'; 

function App() {
  //useState store list
  const [items, setItems] = useState([]);//
  const [inputValue, setInputValue] = useState("");//
  // Function to add an item to the list
  const addItem = () => {
    if (inputValue.trim() !== "") {
      setItems([...items, inputValue]); // Add the new item 
      setInputValue(""); // Clear the input field
    }
  };
   // Function to delete an item from the list
   const deleteItem = (index) => {
    setItems(items.filter((_, i) => i !== index)); // Remove the item at the specified index
  };

  return (
    <div>
       {/* Application Title */}
        <h1>To-Do List</h1>
      {/* ToDoList Component */}
      <ToDoList items={items} deleteItem={deleteItem} />
      {/* InputArea Component */}
      <InputArea addItem={addItem} />
      </div>
  );
}

export default App;
