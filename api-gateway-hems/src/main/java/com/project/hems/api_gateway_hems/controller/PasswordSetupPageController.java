package com.project.hems.api_gateway_hems.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PasswordSetupPageController {

    @GetMapping(value = "/password-setup", produces = MediaType.TEXT_HTML_VALUE)
    public String page(@RequestParam(defaultValue = "") String email) {
        return """
            <!doctype html>
            <html>
              <head><meta charset="utf-8"><title>Password Setup</title></head>
              <body style="font-family: Arial; padding: 24px">
                <h2>Set your password</h2>
                <p>Email: <b>%s</b></p>
                <p>Click the button to receive a password setup email.</p>

                <button onclick="send()">Send password setup email</button>
                <pre id="out" style="margin-top:16px"></pre>

                <script>
                  async function send() {
                    const email = "%s";
                    const res = await fetch("/auth/send-password-setup-email", {
                      method: "POST",
                      headers: {"Content-Type": "application/json"},
                      body: JSON.stringify({email})
                    });
                    const text = await res.text();
                    document.getElementById("out").innerText = text;
                  }
                </script>
              </body>
            </html>
        """.formatted(escape(email), escape(email));
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}
