import React from "react";

function ToDoList({ items, deleteItem }) {
  return (
    <div>
      {items.map((item, index) => (
        <p key={index} onDoubleClick={() => deleteItem(index)}>
          {item}
        </p>
      ))}
    </div>
  );
}

export default ToDoList;