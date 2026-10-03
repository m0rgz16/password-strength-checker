# Security learning and project roadmap

I'm studying a Level 3 T Level in Digital Support Services & Cyber Security and building practical skills alongside my course. This document tracks what I want to understand and improve in my password strength checker project. Items below are **learning goals**, not claims that I've completed them.

## Why password strength matters

A password's security depends on more than including symbols or capital letters. Length, unpredictability, reuse and whether a password has appeared in a data breach all matter. A password-strength checker provides guidance, but it cannot guarantee that a password is secure.

## What I want to investigate

- [ ] Review how the checker scores password length and character variety.
- [ ] Test common passwords, repeated patterns and long passphrases.
- [ ] Make feedback understandable without claiming a password is completely safe.
- [ ] Check that the program doesn't log, store or transmit passwords unnecessarily.
- [ ] Learn about breached-password checking using privacy-preserving methods.
- [ ] Write automated tests for empty input, Unicode, spaces and unusually long input.
- [ ] Document the project's limitations and security assumptions.

## Networking concepts to connect with this project

While preparing for CompTIA Network+, I want to understand how DNS, TCP/IP, TLS and HTTPS protect (or don't protect) data in transit. If I ever make this checker a web application, I'll document whether passwords are processed locally or sent to a server.

## Project journal

For each change, record the date, what I changed, how I tested it and what I learned. Only tick a task once I've actually implemented or verified it.

> Never commit real passwords, credentials, API keys or private user data to GitHub.
