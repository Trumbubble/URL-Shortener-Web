import { useRef, useState } from "react";
import "./App.css";


function App() {
  const homeRef = useRef(null);
  const aboutRef = useRef(null);
  const shortRef = useRef(null);

  const [longUrl, setLongUrl] = useState("");
  const [shortUrl, setShortUrl] = useState("");
  const [status, setStatus] = useState("");
  const [rows, setRows] = useState([]); // recent items
  const [loading, setLoading] = useState(false);

  const scrollToSection = (ref) => {
    if (ref && ref.current) {
      ref.current.scrollIntoView({ behavior: "smooth", block: "start" });
    }
  };

  const generateUrl = async () => {
    if (!longUrl.trim()) {
      setStatus("Please enter a URL");
      return;
    }

    setLoading(true);
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
        setLoading(false);
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
      setStatus("Short URL generated");
      setRows(prev => [
        { id: Date.now(), longurl: longUrl, shorter: tokenOrUrl, shortUrl: finalUrl },
        ...prev
      ]);
      // scroll to result (optional)
      setTimeout(() => {
        const el = document.querySelector(".result");
        if (el) el.scrollIntoView({ behavior: "smooth", block: "center" });
      }, 150);
    } catch (err) {
      console.error("Fetch error:", err);
      setStatus(`Fetch failed: ${err.message}`);
    } finally {
      setLoading(false);
      setLongUrl("");
    }
  };

  const copyToClipboard = async (text) => {
    try {
      await navigator.clipboard.writeText(text);
      setStatus("Copied to clipboard");
    } catch (e) {
      setStatus("Copy failed");
    }
  };

  const RecentTable = () => (
    rows.length === 0 ? (
      <p className="muted">No recent items yet — generate one!</p>
    ) : (
      <div className="table-wrap">
        <table className="table">
          <thead>
            <tr>
              <th>Long URL</th>
              <th>Short URL</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {rows.map(r => (
              <tr key={r.id}>
                <td className="long-cell" title={r.longurl}>
                  {r.longurl}
                </td>
                <td className="short-cell">
                  <a href={r.shortUrl} target="_blank" rel="noreferrer">
                    {r.shortUrl}
                  </a>
                </td>
                <td className="actions-cell">
                  <button className="small" onClick={() => copyToClipboard(r.shortUrl)}>
                    Copy
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    )
  );


  return (
    
    <div className="app-shell">
      {/* NAVIGATION */}
      <nav className="nav">
        <div className="header">
          <div className="logo-title">
            <img
              src="https://cdn-icons-png.flaticon.com/512/7471/7471685.png"
              alt="logo"
              className="logo"
            />
            <h1>ShortLink</h1>
          </div>

          <div className="nav-buttons">
            <button className="button" onClick={() => scrollToSection(homeRef)}>
              Home
            </button>
            <button className="button" onClick={() => scrollToSection(shortRef)}>
              Shorten!
            </button>
            <button className="button" onClick={() => scrollToSection(aboutRef)}>
              About
            </button>
          </div>
        </div>
        <hr></hr>
      </nav>



      <main className="container">
        {/* HOME SECTION */}
        <section ref={homeRef} id="home">

        <br></br>
        <div className="center">
          <img
            src="https://cdn-icons-png.flaticon.com/512/7471/7471685.png"
            alt="logo"
            className="logo-big"
          />
        </div>
        

          <h1 class = "h1" align="center">Shorten your looong links</h1>
          <h3 class = "h3" align="center">Gone are the days of long urls with hundreds of characters. Generate short URLs below </h3>

          <br></br>

          <div className="scroll-buttons">
            <button className="button" onClick={() => scrollToSection(shortRef)}>
              Get Started!
            </button>
          </div>
        </section>

        <br></br>

        <div className="display">
          <img
            src="https://static.vecteezy.com/system/resources/previews/015/181/358/non_2x/short-and-custom-urls-url-shortener-technology-and-generator-scissor-cut-an-address-bar-or-link-to-make-it-shorter-vector.jpg"
            alt="big-image"
            className="big-image"
          />
        </div>

        <br/> <hr/> <br/>

        <section ref={shortRef} id="shorten">

          <div className="center">
            <h2 className="h2">Paste a long URL below and generate a short link.</h2>
          </div>

          <div className="form-column">
            <input
              className="input"
              value={longUrl}
              onChange={(e) => setLongUrl(e.target.value)}
              placeholder="Enter a long URL (https://...)"
              onKeyDown={(e) => { if (e.key === "Enter") generateUrl(); }}
              aria-label="Long URL"
            />
            <button className="button" onClick={generateUrl} disabled={loading}>
              {loading ? "Generating…" : "Generate"}
            </button>
            <div className="h3">{status}</div>

            {shortUrl && (
              <div className="result">
                <a href={shortUrl} target="_blank" rel="noreferrer" className="link">{shortUrl}</a>
                <button className="copy-button" onClick={() => copyToClipboard(shortUrl)}>⧉ Copy</button>
              </div>
            )}
          </div>
          

          <h3 style={{ marginTop: 24 }}>Recent</h3>
          <RecentTable />
        </section>

        {/* ABOUT SECTION (below) */}
        <section ref={aboutRef} id="about" style={{ marginTop: 40 }}>
          <h1>About</h1>
          <p>
            This is a simple URL shortener demo (Spring Boot backend + React frontend).
            Use the Home section above to create short links and view recent entries below.
          </p>
        </section>
      </main>

      <footer>
        <span>© {new Date().getFullYear()} Trumbubble</span>
      </footer>
    </div>
  );
}


export default App;
