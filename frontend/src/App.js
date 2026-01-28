import { useState } from "react";
import "./App.css";

function App() {
  const [longUrl, setLongUrl] = useState("");
  const [shortUrl, setShortUrl] = useState("");
  const [status, setStatus] = useState("");
  const [rows, setRows] = useState([]); // optional datatable rows

  const generateUrl = async () => {
    if (!longUrl.trim()) {
      setStatus("Please enter a URL");
      return;
    }
    setStatus("Posting...");
    setShortUrl("");

    try {
      const res = await fetch("http://localhost:8080/api/urls", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ longUrl })
      });

      setStatus(`HTTP ${res.status}`);

      const ct = (res.headers.get("content-type") || "").toLowerCase();
      let tokenOrUrl;
      if (ct.includes("application/json")) {
        const data = await res.json();
        tokenOrUrl = data.shortUrl || data.shorter || data.short || null;
      } else {
        tokenOrUrl = (await res.text()).trim();
      }

      if (!tokenOrUrl) {
        setStatus("Server returned no short code. See console.");
        console.error("Empty response body:", res);
        return;
      }

      let finalUrl;
      if (/^https?:\/\//i.test(tokenOrUrl)) {
        finalUrl = tokenOrUrl;
      } else {
        const safe = encodeURIComponent(tokenOrUrl.trim());
        finalUrl = `http://localhost:8080/${safe}`;
      }

      setShortUrl(finalUrl);
      setStatus("OK");

      setRows(prev => [
        { id: Date.now(), longurl: longUrl, shorter: tokenOrUrl, shortUrl: finalUrl },
        ...prev
      ]);

    } catch (err) {
      console.error("Fetch error:", err);
      setStatus(`Fetch failed: ${err.message}`);
    }
  };

  return (
    <div className="App">
      <h3>URL Shortener</h3>

      <input
        value={longUrl}
        onChange={(e) => setLongUrl(e.target.value)}
        placeholder="Enter a long URL"
        style={{ width: 450 }}
      />
      <button onClick={generateUrl}>Generate</button>

      <div style={{ marginTop: 10 }}>
        <strong>Status:</strong> {status}
      </div>

      {shortUrl && (
        <div style={{ marginTop: 10 }}>
          <strong>Short URL:</strong>{" "}
          <a href={shortUrl} target="_blank" rel="noreferrer">
            {shortUrl}
          </a>
        </div>
      )}

      
      <h4>Recent: </h4>
      {rows.length > 0 && (
        <div style={{ marginTop: 20, width: "80%", display: "flex", justifyContent: "center" }}>
          <table border="1" cellPadding="6">
            <thead>
              <tr><th>Long URL</th><th>Link</th></tr>
            </thead>
            <tbody>
              {rows.map(r => (
                <tr key={r.id}>
                  <td>{r.longurl}</td>
                  <td>
                    <a href={r.shortUrl} target="_blank" rel="noreferrer">
                      {r.shortUrl}
                    </a>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default App;
