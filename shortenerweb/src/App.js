import logo from './logo.svg';
import {Button, useState} from 'react';
import './App.css';

function App() {

  const [longUrl, setLongUrl] = useState("");

  const generateUrl = async () => {
    body: JSON.stringify({ longUrl })
  }

  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.js</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
        <input
          type="text"
          value={longUrl}
          onChange={(e) => setLongUrl(e.target.value)}
          placeholder="Enter long URL"
        />

        <button onClick={generateUrl}>Generate</button>
        {/* <Button
          onPress={generateLink}
          title="Learn More"
          color="#841584"
          accessbilityLabel="Learn more about this purple button"
        /> */}
      </header>
    </div>
  );
}


export default App;
