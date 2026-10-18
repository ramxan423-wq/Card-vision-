# CardVision AI — Complete Feature Specification (CollX + Ludex class)
Status: product requirements and implementation backlog, NOT a claim that these features already work.
Design: original CardVision AI brand; inspired by modern card collecting apps, not pixel-for-pixel copied. No existing functionality may be deleted without explicit approval.
Release rule: DO NOT build/publish APK until user explicitly requests.

## 01 Camera & recognition
- [ ] Live camera with permission flow, autofocus, flash, gallery import
- [ ] Front/back capture, auto card edge detection, perspective correction, glare/blur detection
- [ ] Single scan and batch scan, duplicates warning, scan queue
- [x] Local gallery OCR (ML Kit; device validation pending)
- [ ] On-device image embeddings + server catalogue candidate search; confidence score and user confirmation
- [ ] Identify player, team, sport/game, brand, set, year, card number, insert, parallel, color, serial number, autograph, memorabilia, rookie, edition
- [ ] Support basketball, baseball, American football, hockey, soccer, racing, Pokémon, Magic, Yu-Gi-Oh!, other TCG
- [ ] Unknown card/manual correction workflow, feedback to improve catalogue

## 02 Card catalogue & data quality
- [ ] Normalized sports/TCG catalogue, edition/variant taxonomy, provenance, aliases
- [ ] Search/filter by player, team, set, year, card number, rarity, parallel
- [ ] Set checklist, completion progress, missing cards, wish list
- [ ] Legal/licensed ingestion, scheduled updates, duplicate merge, attribution
- [ ] Offline cache and online refresh, multilingual labels

## 03 Pricing & analytics
- [ ] Licensed sold-comps from marketplaces and authorized providers
- [ ] Price by exact variant, grade, raw/graded, currency, condition, sale date
- [ ] Median/trimmed comps, outlier removal, low-confidence indication
- [ ] 7/30/90/365-day charts, price change %, comparable sale links
- [ ] Collection total, cost basis, profit/loss, gain/loss charts
- [ ] Watchlists, price alerts, trending cards, market movers
- [x] Manual price and local total; NOT live price data

## 04 Condition & grading assistant
- [ ] Centering analysis with visual overlay and ratio
- [ ] Corners/edges/surface defect detection; multiple-angle photo guidance
- [ ] Explainable non-official condition estimate and confidence
- [ ] Grading provider, slab certificate, grade, submission tracking
- [ ] Never label assistant output as an official PSA/BGS/CGC grade
- [ ] Counterfeit/alteration suspicion indicator with uncertainty warning

## 05 Collection management
- [x] Add, list, search, favorite, delete, local save
- [ ] Rich card editor, photo storage, multiple copies, quantity
- [ ] Folders, binders, custom tags, notes, purchase date/place, purchase price
- [ ] Sorting, advanced filters, saved searches, custom collection sharing
- [ ] Import/export CSV/JSON, reliable backup/restore, conflict resolution
- [ ] Public/private collection, portfolio and set completion dashboards
- [ ] Trade inventory, wishlist, trade value comparison, insurance export

## 06 Marketplace
- [ ] Listings with front/back photos, price, currency, condition, shipping
- [ ] Seller storefront/profile, ratings, report/block, moderation
- [ ] Buyer offers, counteroffers, bulk offers, carts, saved listings
- [ ] In-app chat, attachments, notifications, dispute workflow
- [ ] Payments through approved provider, payout/KYC, tax and refund policies
- [ ] Order tracking, shipping labels, fees, fraud protections
- [ ] Country/age/legal compliance before enabling transactions

## 07 eBay & integrations
- [ ] OAuth account linking and token refresh
- [ ] Inventory mapping, listing drafts, publish/update/end listings
- [ ] Sync price, quantity, orders with conflict safeguards
- [ ] Respect eBay developer terms and rate limits
- [ ] No scraping or credential collection

## 08 Account, community & cloud
- [ ] Email/password and Google sign-in, verified email, password reset
- [ ] Account delete/data export, privacy choices, consent and age handling
- [ ] Cloud sync with offline-first queue, conflict resolution
- [ ] Public profiles, follow, posts, likes, comments, collection sharing
- [ ] Push notifications for messages, offers, price alerts, set releases
- [ ] Admin panel, abuse reports, moderation audit trail

## 09 Premium
- [ ] Free vs Pro entitlements defined transparently
- [ ] Google Play Billing subscriptions, restore purchase, server verification
- [ ] Limits for scans, batch scan, historical charts, exports and advanced AI
- [ ] Subscription cancellation, regional prices, trials if approved
- [ ] No fake purchase buttons; no charge until verified end-to-end

## 10 UI/UX
- [ ] Premium original light theme: white, soft-gray cards, blue accent; optional dark theme
- [ ] Bottom navigation: Home / Scan / Collection / Market / Profile
- [ ] Onboarding, dashboard, scanner, scan results, card detail, charts
- [ ] Collection, binders, set checklist, watchlist, listings, messages, settings
- [ ] Accessibility, large text, screen reader, empty/error/loading states
- [ ] Turkish/English initially; localization architecture for more languages
- [ ] Responsive phone/tablet layouts, fast image loading, cached thumbnails

## 11 Security, testing & operations
- [ ] Secure server API, database, object storage, auth, audit logs
- [ ] API key protection, rate limiting, spam/fraud detection
- [ ] Privacy policy, terms, marketplace compliance and content rights
- [ ] Unit/instrumented tests, camera tests, offline tests, migrations
- [ ] CI checks; crash reporting and opt-in diagnostics
- [ ] Performance budget, accessibility QA, staged releases

## Engineering modules
- Android: app, scanner, recognition, catalogue, pricing, collection, grading, marketplace, chat, account, subscription, settings
- Backend: auth, catalogue, price-ingestion, inventory, marketplace, messaging, notification, moderation
- Storage: SQL relational DB + indexed card search + image/object storage
- Integration adapters: authorized card data, sold comps, eBay, Play Billing, push

## Acceptance gates
1. Each feature must have real UI, persistence/network behavior, tests and documented error handling.
2. Unconnected external services are labeled unavailable, never mocked as live.
3. All previous working features remain intact.
4. No APK build or release without explicit user instruction.
