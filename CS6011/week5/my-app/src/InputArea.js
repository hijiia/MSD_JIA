import React, { useRef } from 'react';

function InputArea({ addItem }) {
  // Create a reference for the textarea
  const inputTextArea = useRef();

  // Callback to handle adding the item
  const handleAddItem = () => {
    // Get the text from the textarea
    const newItem = inputTextArea.current.value.trim();

    if (newItem) {
      // Call the addItem function passed down via props
      addItem(newItem);
      // Clear the textarea after adding the item
      inputTextArea.current.value = '';
    }
  };

  return (
    <div>
      <textarea
        ref={inputTextArea}
        placeholder="Type a new task..."
        rows="4"
        cols="50"
      />
      <button onClick={handleAddItem}>Add</button>
    </div>
  );
}

export default InputArea;