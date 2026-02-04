package com.project.hems.api_gateway_hems.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppLoginPageController {

    @GetMapping(value = "/login", produces = MediaType.TEXT_HTML_VALUE)
    public String loginPage() {
        return """
        <!doctype html>
        <html>
        <head>
          <meta charset="utf-8" />
          <title>Login</title>
          <style>
            body { font-family: Arial, sans-serif; padding: 28px; background: #f7f7f7; }
            .card { max-width: 420px; margin: 40px auto; background: white; padding: 22px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,.08); }
            input { width: 100%; padding: 10px; margin: 8px 0; font-size: 14px; }
            button { width: 100%; padding: 12px; font-size: 15px; cursor: pointer; }
            .row { margin-top: 10px; }
            .msg { margin-top: 12px; padding: 10px; border-radius: 8px; display:none; }
            .msg.ok { background: #e7f7ea; }
            .msg.err { background: #ffecec; }
            .small { color: #555; font-size: 13px; margin-top: 10px; }
            a { color: #0b66ff; text-decoration: none; }
          </style>
        </head>
        <body>
          <div class="card">
            <h2>Login</h2>

            <label>Email</label>
            <input id="email" type="email" placeholder="you@example.com" />

            <label>Password</label>
            <input id="password" type="password" placeholder="Your password" />

            <div class="row">
              <button onclick="handleLogin()">Continue</button>
            </div>

            <div id="msg" class="msg"></div>

            <div class="small">
              If you signed up with Google/GitHub and want password login, we will send a password setup email.
            </div>

            <div class="small" style="margin-top:14px;">
              Or use social login:
              <a href="/oauth2/authorization/auth0">Continue with Auth0 (Google/GitHub)</a>
            </div>
          </div>

          <script>
            function showMsg(text, type) {
              const el = document.getElementById("msg");
              el.className = "msg " + (type || "ok");
              el.innerText = text;
              el.style.display = "block";
            }

            async function handleLogin() {
              const email = document.getElementById("email").value.trim();
              const password = document.getElementById("password").value; // not sent to backend (not needed)

              if (!email) {
                showMsg("Email is required", "err");
                return;
              }

              // 1) Ask backend if this email is social-only; if yes -> send password setup email
              try {
                const res = await fetch("/auth/check-email-and-handle", {
                  method: "POST",
                  headers: { "Content-Type": "application/json" },
                  body: JSON.stringify({ email })
                });

                const data = await res.json();

                if (data.status === "PASSWORD_SETUP_EMAIL_SENT") {
                  showMsg(data.message + " (Check inbox/spam)", "ok");
                  return; // STOP. Don't redirect to Auth0 DB login.
                }

                if (data.status !== "OK") {
                  showMsg(data.message || "Something went wrong", "err");
                  return;
                }

                // 2) Not social-only -> proceed to normal Auth0 login flow
                // Redirect to Spring OAuth2 login endpoint (Auth0 Universal Login)
                window.location.href = "/auth/start-login?email=" + encodeURIComponent(email);

              } catch (e) {
                showMsg("Network error. Please try again.", "err");
              }
            }
          </script>
        </body>
        </html>
        """;
    }
}
