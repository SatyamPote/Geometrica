            "prism", "0", "boss0" -> { // #1 PRISM (ID: prism)
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.48).toFloat())
                path.lineTo((x + bw * 0.4).toFloat(), (y).toFloat())
                path.lineTo((x).toFloat(), (y + bw * 0.48).toFloat())
                path.lineTo((x - bw * 0.4).toFloat(), (y).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.28).toFloat())
                path.lineTo((x + bw * 0.2).toFloat(), (y).toFloat())
                path.lineTo((x).toFloat(), (y + bw * 0.28).toFloat())
                path.lineTo((x - bw * 0.2).toFloat(), (y).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x - bw * 0.4).toFloat(), (y).toFloat(), (x + bw * 0.4).toFloat(), (y).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x).toFloat(), (y - bw * 0.48).toFloat(), (x).toFloat(), (y + bw * 0.48).toFloat(), stroke)
            }
            "cascade", "1", "boss1" -> { // #2 CASCADE (ID: cascade)
                for (i in 0 until 5) {
                    val segY = (y - bw * 0.4f + i * bw * 0.18f)
                    val gap = (i - 2) * 12f
                    val left_x2 = (x - bw * 0.45f).coerceAtLeast(x + gap - 12f)
                    val right_x1 = (x + bw * 0.45f).coerceAtMost(x + gap + 12f)
                    c.drawRect(x - bw * 0.45f, segY, left_x2, segY + bw * 0.1f, fill.apply { color = col })
                    c.drawRect(right_x1, segY, x + bw * 0.45f, segY + bw * 0.1f, fill.apply { color = col })
                }
            }
            "gauntlet", "2", "boss2" -> { // #3 GAUNTLET (ID: gauntlet)
                c.drawRect((x-bw*0.45).toFloat(), (y-bw*0.35).toFloat(), (x-bw*0.15).toFloat(), (y+bw*0.35).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.45).toFloat(), (y-bw*0.35).toFloat(), (x-bw*0.15).toFloat(), (y+bw*0.35).toFloat(), stroke)
                c.drawRect((x+bw*0.15).toFloat(), (y-bw*0.35).toFloat(), (x+bw*0.45).toFloat(), (y+bw*0.35).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x+bw*0.15).toFloat(), (y-bw*0.35).toFloat(), (x+bw*0.45).toFloat(), (y+bw*0.35).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawLine((x - bw * 0.15).toFloat(), (y).toFloat(), (x - bw * 0.05).toFloat(), (y).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawLine((x + bw * 0.05).toFloat(), (y).toFloat(), (x + bw * 0.15).toFloat(), (y).toFloat(), stroke)
            }
            "vortex", "3", "boss3" -> { // #4 VORTEX (ID: vortex)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.42).toFloat(), (y-bw*0.42).toFloat(), (x+bw*0.42).toFloat(), (y+bw*0.42).toFloat(), stroke)
                stroke.color = GRAY; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.26).toFloat(), (y-bw*0.26).toFloat(), (x+bw*0.26).toFloat(), (y+bw*0.26).toFloat(), stroke)
                c.drawOval((x-bw*0.1).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.1).toFloat(), (y+bw*0.1).toFloat(), fill.apply { color = col })
                for (i in 0 until 6) {
                val a = (i * PI / 3).toFloat()
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawArc((x-bw*0.35).toFloat(), (y-bw*0.35).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.35).toFloat(), (i*60).toFloat(), 45f, false, stroke)
            }
            }
            "beacon", "4", "boss4" -> { // #5 BEACON (ID: beacon)
                c.drawRect((x-bw*0.12).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.45).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.12).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.45).toFloat(), stroke)
                c.drawOval((x-bw*0.2).toFloat(), (y-bw*0.42).toFloat(), (x+bw*0.2).toFloat(), (y-bw*0.18).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.06).toFloat(), (y-bw*0.33).toFloat(), (x+bw*0.06).toFloat(), (y-bw*0.21).toFloat(), fill.apply { color = inv })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.35).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.1).toFloat(), stroke)
            }
            "splitter", "5", "boss5" -> { // #6 SPLITTER (ID: splitter)
                path.reset()
                path.moveTo((x - bw * 0.05).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x - bw * 0.42).toFloat(), (y + bw * 0.3).toFloat())
                path.lineTo((x - bw * 0.05).toFloat(), (y + bw * 0.3).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                path.reset()
                path.moveTo((x + bw * 0.05).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x + bw * 0.42).toFloat(), (y + bw * 0.3).toFloat())
                path.lineTo((x + bw * 0.05).toFloat(), (y + bw * 0.3).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                c.drawOval((x-bw*0.18).toFloat(), (y+bw*0.05).toFloat(), (x-bw*0.08).toFloat(), (y+bw*0.15).toFloat(), fill.apply { color = col })
                c.drawOval((x+bw*0.08).toFloat(), (y+bw*0.05).toFloat(), (x+bw*0.18).toFloat(), (y+bw*0.15).toFloat(), fill.apply { color = col })
            }
            "orbit", "6", "boss6" -> { // #7 ORBIT (ID: orbit)
                c.drawOval((x-bw*0.24).toFloat(), (y-bw*0.24).toFloat(), (x+bw*0.24).toFloat(), (y+bw*0.24).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.24).toFloat(), (y-bw*0.24).toFloat(), (x+bw*0.24).toFloat(), (y+bw*0.24).toFloat(), stroke)
                c.drawOval((x-bw*0.08).toFloat(), (y-bw*0.08).toFloat(), (x+bw*0.08).toFloat(), (y+bw*0.08).toFloat(), fill.apply { color = col })
                for (i in 0 until 8) {
                val a = (i * PI / 4).toFloat()
                // tx, ty = cx + math.cos(a)*s*0.38, cy + math.sin(a)*s*0.38
                path.reset()
                path.moveTo((tx).toFloat(), (ty - 6).toFloat())
                path.lineTo((tx + 6).toFloat(), (ty + 6).toFloat())
                path.lineTo((tx - 6).toFloat(), (ty + 6).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            }
            "sawtooth", "7", "boss7" -> { // #8 SAWTOOTH (ID: sawtooth)
                path.reset()
                for (i in 0 until 12) {
                    val r = if (i % 2 == 0) bw * 0.42f else bw * 0.24f
                    val a = i * PI / 6.0
                    val px = (x + cos(a) * r).toFloat()
                    val py = (y + sin(a) * r).toFloat()
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                c.drawOval(x - bw * 0.1f, y - bw * 0.1f, x + bw * 0.1f, y + bw * 0.1f, fill.apply { color = col })
            }
            "tower", "8", "boss8" -> { // #9 TOWER (ID: tower)
                c.drawRect((x-bw*0.25).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.45).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.25).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.45).toFloat(), stroke)
                for (row in 0 until 4) {
                for (col in 0 until 2) {
                val wx = (x - bw*0.15 + col * bw*0.2).toFloat()
                val wy = (y - bw*0.35 + row * bw*0.2).toFloat()
                c.drawRect((wx-8).toFloat(), (wy-8).toFloat(), (wx+8).toFloat(), (wy+8).toFloat(), fill.apply { color = col })
            }
            }
            }
            "mesh", "9", "boss9" -> { // #10 MESH (ID: mesh)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.4).toFloat(), (y-bw*0.4).toFloat(), (x+bw*0.4).toFloat(), (y+bw*0.4).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x - bw * 0.4).toFloat(), (y).toFloat(), (x + bw * 0.4).toFloat(), (y).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x).toFloat(), (y - bw * 0.4).toFloat(), (x).toFloat(), (y + bw * 0.4).toFloat(), stroke)
                c.drawRect((x-bw*0.2).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.2).toFloat(), fill.apply { color = col })
                c.drawOval((x-5).toFloat(), (y-5).toFloat(), (x+5).toFloat(), (y+5).toFloat(), fill.apply { color = inv })
            }
            "flare", "10", "boss10" -> { // #11 FLARE (ID: flare)
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x - bw * 0.38).toFloat(), (y + bw * 0.35).toFloat())
                path.lineTo((x).toFloat(), (y + bw * 0.15).toFloat())
                path.lineTo((x + bw * 0.38).toFloat(), (y + bw * 0.35).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                for (i in floatArrayOf(-bw*0.25f, 0f, bw*0.25f)) {
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x + i).toFloat(), (y - bw * 0.45).toFloat(), (x + i * 1.8).toFloat(), (y - bw * 0.1).toFloat(), stroke)
            }
            }
            "delta", "11", "boss11" -> { // #12 DELTA (ID: delta)
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.4).toFloat())
                path.lineTo((x - bw * 0.45).toFloat(), (y - bw * 0.25).toFloat())
                path.lineTo((x).toFloat(), (y - bw * 0.05).toFloat())
                path.lineTo((x + bw * 0.45).toFloat(), (y - bw * 0.25).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                c.drawRect((x-bw*0.08).toFloat(), (y-bw*0.35).toFloat(), (x+bw*0.08).toFloat(), (y+bw*0.2).toFloat(), fill.apply { color = col })
            }
            "web", "12", "boss12" -> { // #13 WEB (ID: web)
                for (r in floatArrayOf(0.42f, 0.28f, 0.14f)) {
                    stroke.color = col; stroke.strokeWidth = 2f
                    c.drawOval(x - bw * r, y - bw * r, x + bw * r, y + bw * r, stroke)
                }
                for (i in 0 until 8) {
                    val a = (i * PI / 4.0).toFloat()
                    stroke.color = col; stroke.strokeWidth = 2f
                    c.drawLine(x, y, (x + cos(a.toDouble()) * bw * 0.42f).toFloat(), (y + sin(a.toDouble()) * bw * 0.42f).toFloat(), stroke)
                }
            }
            "prismatic", "13", "boss13" -> { // #14 PRISMATIC (ID: prismatic)
                path.reset()
                for (i in 0 until 6) {
                    val a = i * PI / 3.0
                    val px = (x + cos(a) * bw * 0.4f).toFloat()
                    val py = (y + sin(a) * bw * 0.4f).toFloat()
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                path.reset()
                path.moveTo(x, y - bw * 0.2f)
                path.lineTo(x + bw * 0.18f, y)
                path.lineTo(x, y + bw * 0.2f)
                path.lineTo(x - bw * 0.18f, y)
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            "helix", "14", "boss14" -> { // #15 HELIX (ID: helix)
                for (i in 0 until 5) {
                val segY = (y - bw*0.36 + i * bw*0.18).toFloat()
                val w = (bw * 0.35).toFloat()
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-w).toFloat(), (y-bw*0.06).toFloat(), (x+w).toFloat(), (y+bw*0.06).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x - bw * 0.25).toFloat(), (y - bw * 0.36).toFloat(), (x + bw * 0.25).toFloat(), (y + bw * 0.36).toFloat(), stroke)
            }
            }
            "lockbox", "15", "boss15" -> { // #16 LOCKBOX (ID: lockbox)
                c.drawRect((x-bw*0.35).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.42).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.35).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.42).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 5f
                c.drawArc((x-bw*0.22).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.22).toFloat(), (y).toFloat(), (180).toFloat(), ((360).toFloat() - (180).toFloat()), false, stroke)
                c.drawOval((x-8).toFloat(), (y+bw*0.1-8).toFloat(), (x+8).toFloat(), (y+bw*0.1+8).toFloat(), fill.apply { color = col })
                path.reset()
                path.moveTo((x - 4).toFloat(), (y + bw * 0.1).toFloat())
                path.lineTo((x + 4).toFloat(), (y + bw * 0.1).toFloat())
                path.lineTo((x).toFloat(), (y + bw * 0.25).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            "flareon", "16", "boss16" -> { // #17 FLAREON (ID: flareon)
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x - bw * 0.45).toFloat(), (y).toFloat())
                path.lineTo((x - bw * 0.15).toFloat(), (y + bw * 0.35).toFloat())
                path.lineTo((x).toFloat(), (y + bw * 0.1).toFloat())
                path.lineTo((x + bw * 0.35).toFloat(), (y + bw * 0.35).toFloat())
                path.lineTo((x + bw * 0.45).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
                c.drawOval((x-bw*0.1).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.1).toFloat(), (y+bw*0.1).toFloat(), fill.apply { color = col })
            }
            "obsidian", "17", "boss17" -> { // #18 OBSIDIAN (ID: obsidian)
                path.reset()
                path.moveTo(x, y - bw * 0.45f)
                path.lineTo(x + bw * 0.35f, y - bw * 0.25f)
                path.lineTo(x + bw * 0.45f, y + bw * 0.1f)
                path.lineTo(x + bw * 0.2f, y + bw * 0.42f)
                path.lineTo(x - bw * 0.3f, y + bw * 0.35f)
                path.lineTo(x - bw * 0.45f, y - bw * 0.15f)
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                path.reset()
                path.moveTo(x - bw * 0.1f, y - bw * 0.2f)
                path.lineTo(x + bw * 0.15f, y - bw * 0.1f)
                path.lineTo(x, y + bw * 0.2f)
                path.lineTo(x - bw * 0.2f, y + bw * 0.1f)
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            "glyph", "18", "boss18" -> { // #19 GLYPH (ID: glyph)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.32).toFloat(), (y-bw*0.38).toFloat(), (x+bw*0.32).toFloat(), (y+bw*0.38).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x - bw * 0.32).toFloat(), (y - bw * 0.38).toFloat(), (x + bw * 0.32).toFloat(), (y + bw * 0.38).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x + bw * 0.32).toFloat(), (y - bw * 0.38).toFloat(), (x - bw * 0.32).toFloat(), (y + bw * 0.38).toFloat(), stroke)
                c.drawOval((x-bw*0.12).toFloat(), (y-bw*0.12).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.12).toFloat(), fill.apply { color = col })
            }
            "siphon", "19", "boss19" -> { // #20 SIPHON (ID: siphon)
                path.reset()
                path.moveTo((x - bw * 0.35).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x + bw * 0.35).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x + bw * 0.06).toFloat(), (y).toFloat())
                path.lineTo((x + bw * 0.35).toFloat(), (y + bw * 0.4).toFloat())
                path.lineTo((x - bw * 0.35).toFloat(), (y + bw * 0.4).toFloat())
                path.lineTo((x - bw * 0.06).toFloat(), (y).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                c.drawOval((x-bw*0.12).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.12).toFloat(), (y-bw*0.15).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.12).toFloat(), (y+bw*0.15).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.25).toFloat(), fill.apply { color = col })
            }
            "pulse", "20", "boss20" -> { // #21 PULSE (ID: pulse)
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawOval((x-bw*0.42).toFloat(), (y-bw*0.42).toFloat(), (x+bw*0.42).toFloat(), (y+bw*0.42).toFloat(), stroke)
                stroke.color = GRAY; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.22).toFloat(), (y-bw*0.22).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.22).toFloat(), stroke)
                c.drawOval((x-bw*0.08).toFloat(), (y-bw*0.08).toFloat(), (x+bw*0.08).toFloat(), (y+bw*0.08).toFloat(), fill.apply { color = col })
            }
            "spirebreak", "21", "boss21" -> { // #22 SPIREBREAK (ID: spirebreak)
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x - bw * 0.15).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + bw * 0.15).toFloat(), (y - bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                c.drawRect((x-bw*0.22).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.12).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.22).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.12).toFloat(), stroke)
                c.drawRect((x-bw*0.32).toFloat(), (y+bw*0.18).toFloat(), (x+bw*0.32).toFloat(), (y+bw*0.42).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.32).toFloat(), (y+bw*0.18).toFloat(), (x+bw*0.32).toFloat(), (y+bw*0.42).toFloat(), stroke)
            }
            "facet", "22", "boss22" -> { // #23 FACET (ID: facet)
                path.reset()
                for (i in 0 until 8) {
                    val a = i * PI / 4.0
                    val px = (x + cos(a) * bw * 0.38f).toFloat()
                    val py = (y + sin(a) * bw * 0.38f).toFloat()
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                stroke.strokeWidth = 1.5f
                for (i in 0 until 8) {
                    val a = i * PI / 4.0
                    c.drawLine(x, y, (x + cos(a) * bw * 0.38f).toFloat(), (y + sin(a) * bw * 0.38f).toFloat(), stroke)
                }
            }
            "vortex2", "23", "boss23" -> { // #24 VORTEX2 (ID: vortex2)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.42).toFloat(), (y-bw*0.22).toFloat(), (x).toFloat(), (y+bw*0.22).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x).toFloat(), (y-bw*0.22).toFloat(), (x+bw*0.42).toFloat(), (y+bw*0.22).toFloat(), stroke)
                c.drawOval((x-bw*0.21-6).toFloat(), (y-6).toFloat(), (x-bw*0.21+6).toFloat(), (y+6).toFloat(), fill.apply { color = col })
                c.drawOval((x+bw*0.21-6).toFloat(), (y-6).toFloat(), (x+bw*0.21+6).toFloat(), (y+6).toFloat(), fill.apply { color = col })
            }
            "cradle", "24", "boss24" -> { // #25 CRADLE (ID: cradle)
                stroke.color = col; stroke.strokeWidth = 5f
                c.drawArc((x-bw*0.4).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.4).toFloat(), (y+bw*0.42).toFloat(), (0).toFloat(), ((180).toFloat() - (0).toFloat()), false, stroke)
                c.drawOval((x-bw*0.15).toFloat(), (y-bw*0.05).toFloat(), (x+bw*0.15).toFloat(), (y+bw*0.25).toFloat(), fill.apply { color = col })
                c.drawOval((x-5).toFloat(), (y+bw*0.1-5).toFloat(), (x+5).toFloat(), (y+bw*0.1+5).toFloat(), fill.apply { color = inv })
            }
            "skyline", "25", "boss25" -> { // #26 SKYLINE (ID: skyline)
                val heights = floatArrayOf(0.25f, 0.45f, 0.35f, 0.5f, 0.3f)
                for (i in 0 until 5) {
                    val hVal = heights[i]
                    val segX = x - bw * 0.4f + i * bw * 0.18f
                    c.drawRect(segX, y + bw * 0.4f - bw * hVal, segX + bw * 0.15f, y + bw * 0.4f, fill.apply { color = bg })
                    stroke.color = col; stroke.strokeWidth = 2f
                    c.drawRect(segX, y + bw * 0.4f - bw * hVal, segX + bw * 0.15f, y + bw * 0.4f, stroke)
                    c.drawRect(segX + 4f, y + bw * 0.4f - bw * hVal + 6f, segX + bw * 0.15f - 4f, y + bw * 0.4f - bw * hVal + 14f, fill.apply { color = col })
                }
            }
            "echo", "26", "boss26" -> { // #27 ECHO (ID: echo)
                path.reset()
                path.moveTo((x - bw * 0.45).toFloat(), (y + bw * 0.3).toFloat())
                path.lineTo((x).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x + bw * 0.45).toFloat(), (y + bw * 0.3).toFloat())
                path.lineTo((x + bw * 0.3).toFloat(), (y + bw * 0.35).toFloat())
                path.lineTo((x).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x - bw * 0.3).toFloat(), (y + bw * 0.35).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                c.drawOval((x-8).toFloat(), (y-bw*0.15).toFloat(), (x+8).toFloat(), (y-bw*0.01).toFloat(), fill.apply { color = col })
            }
            "needle", "27", "boss27" -> { // #28 NEEDLE (ID: needle)
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawLine((x).toFloat(), (y - bw * 0.45).toFloat(), (x).toFloat(), (y + bw * 0.45).toFloat(), stroke)
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x - bw * 0.12).toFloat(), (y - bw * 0.25).toFloat())
                path.lineTo((x).toFloat(), (y - bw * 0.05).toFloat())
                path.lineTo((x + bw * 0.12).toFloat(), (y - bw * 0.25).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                c.drawRect((x-bw*0.2).toFloat(), (y+bw*0.35).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.45).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.2).toFloat(), (y+bw*0.35).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.45).toFloat(), stroke)
            }
            "coil", "28", "boss28" -> { // #29 COIL (ID: coil)
                for (i in 0 until 6) {
                val segY = (y - bw*0.38 + i * bw*0.14).toFloat()
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x - bw * 0.3).toFloat(), (y).toFloat(), (x + bw * 0.3).toFloat(), (y + bw * 0.07).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x + bw * 0.3).toFloat(), (y + bw * 0.07).toFloat(), (x - bw * 0.3).toFloat(), (y + bw * 0.14).toFloat(), stroke)
            }
            }
            "sentinel-guard", "29", "boss29" -> { // #30 SENTINEL-GUARD (ID: sentinel-guard)
                c.drawOval((x-bw*0.18).toFloat(), (y-bw*0.18).toFloat(), (x+bw*0.18).toFloat(), (y+bw*0.18).toFloat(), fill.apply { color = col })
                c.drawOval((x-6).toFloat(), (y-6).toFloat(), (x+6).toFloat(), (y+6).toFloat(), fill.apply { color = inv })
                for (i in 0 until 4) {
                val a = (i * PI / 2).toFloat()
                // px, py = cx + math.cos(a)*s*0.32, cy + math.sin(a)*s*0.32
                c.drawRect((px-8).toFloat(), (py-8).toFloat(), (px+8).toFloat(), (py+8).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((px-8).toFloat(), (py-8).toFloat(), (px+8).toFloat(), (py+8).toFloat(), stroke)
            }
            }
            "rampart", "30", "boss30" -> { // #31 RAMPART (ID: rampart)
                c.drawRect((x-bw*0.45).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.45).toFloat(), (y+bw*0.35).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.45).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.45).toFloat(), (y+bw*0.35).toFloat(), stroke)
                for (i in 0 until 4) {
                val segX = (x - bw*0.38 + i * bw*0.22).toFloat()
                c.drawRect((x).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.12).toFloat(), (y-bw*0.1).toFloat(), fill.apply { color = col })
            }
            }
            "sentinel-core", "31", "boss31" -> { // #32 SENTINEL-CORE (ID: sentinel-core)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.42).toFloat(), (y-bw*0.42).toFloat(), (x+bw*0.42).toFloat(), (y+bw*0.42).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.26).toFloat(), (y-bw*0.26).toFloat(), (x+bw*0.26).toFloat(), (y+bw*0.26).toFloat(), stroke)
                c.drawRect((x-bw*0.12).toFloat(), (y-bw*0.12).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.12).toFloat(), fill.apply { color = col })
            }
            "havoc", "32", "boss32" -> { // #33 HAVOC (ID: havoc)
                path.reset()
                for (i in 0 until 16) {
                    val r = if (i % 2 == 0) bw * 0.45f else bw * 0.18f
                    val a = i * PI / 8.0
                    val px = (x + cos(a) * r).toFloat()
                    val py = (y + sin(a) * r).toFloat()
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close()
                c.drawPath(path, fill.apply { color = col })
                c.drawOval(x - bw * 0.08f, y - bw * 0.08f, x + bw * 0.08f, y + bw * 0.08f, fill.apply { color = inv })
            }
            "sentinel-wing", "33", "boss33" -> { // #34 SENTINEL-WING (ID: sentinel-wing)
                path.reset()
                path.moveTo((x - bw * 0.45).toFloat(), (y - bw * 0.3).toFloat())
                path.lineTo((x + bw * 0.35).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x + bw * 0.2).toFloat(), (y + bw * 0.35).toFloat())
                path.lineTo((x - bw * 0.15).toFloat(), (y + bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x - bw * 0.45).toFloat(), (y - bw * 0.3).toFloat(), (x + bw * 0.2).toFloat(), (y + bw * 0.35).toFloat(), stroke)
                c.drawOval((x-8).toFloat(), (y-8).toFloat(), (x+8).toFloat(), (y+8).toFloat(), fill.apply { color = col })
            }
            "sentinel-tower", "34", "boss34" -> { // #35 SENTINEL-TOWER (ID: sentinel-tower)
                c.drawRect((x-bw*0.25).toFloat(), (y).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.42).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.25).toFloat(), (y).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.42).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawLine((x).toFloat(), (y).toFloat(), (x).toFloat(), (y - bw * 0.45).toFloat(), stroke)
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x + bw * 0.3).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x).toFloat(), (y - bw * 0.25).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            "cataclysm", "35", "boss35" -> { // #36 CATACLYSM (ID: cataclysm)
                c.drawOval((x-bw*0.45).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.45).toFloat(), (y-bw*0.05).toFloat(), fill.apply { color = col })
                c.drawRect((x-bw*0.12).toFloat(), (y-bw*0.05).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.45).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.12).toFloat(), (y-bw*0.05).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.45).toFloat(), stroke)
                c.drawOval((x-bw*0.25).toFloat(), (y-bw*0.32).toFloat(), (x-bw*0.15).toFloat(), (y-bw*0.22).toFloat(), fill.apply { color = inv })
                c.drawOval((x+bw*0.15).toFloat(), (y-bw*0.32).toFloat(), (x+bw*0.25).toFloat(), (y-bw*0.22).toFloat(), fill.apply { color = inv })
            }
            "sentinel-spike", "36", "boss36" -> { // #37 SENTINEL-SPIKE (ID: sentinel-spike)
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.48).toFloat())
                path.lineTo((x - bw * 0.12).toFloat(), (y + bw * 0.15).toFloat())
                path.lineTo((x + bw * 0.12).toFloat(), (y + bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                c.drawOval((x-bw*0.3).toFloat(), (y+bw*0.15).toFloat(), (x+bw*0.3).toFloat(), (y+bw*0.42).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.3).toFloat(), (y+bw*0.15).toFloat(), (x+bw*0.3).toFloat(), (y+bw*0.42).toFloat(), stroke)
                c.drawOval((x-6).toFloat(), (y-bw*0.1).toFloat(), (x+6).toFloat(), (y).toFloat(), fill.apply { color = inv })
            }
            "sentinel-ring", "37", "boss37" -> { // #38 SENTINEL-RING (ID: sentinel-ring)
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawOval((x-bw*0.38).toFloat(), (y-bw*0.38).toFloat(), (x+bw*0.38).toFloat(), (y+bw*0.38).toFloat(), stroke)
                c.drawOval((x-bw*0.15).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.15).toFloat(), (y+bw*0.15).toFloat(), fill.apply { color = col })
                for (i in 0 until 4) {
                val a = (i * PI / 2 + 0.3).toFloat()
                c.drawOval((x+cos(a)*bw*0.38-8).toFloat(), (y+sin(a)*bw*0.38-8).toFloat(), (x+cos(a)*bw*0.38+8).toFloat(), (y+sin(a)*bw*0.38+8).toFloat(), fill.apply { color = col })
            }
            }
            "sentinel-cross", "38", "boss38" -> { // #39 SENTINEL-CROSS (ID: sentinel-cross)
                c.drawRect((x-bw*0.42).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.42).toFloat(), (y+bw*0.1).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.42).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.42).toFloat(), (y+bw*0.1).toFloat(), stroke)
                c.drawRect((x-bw*0.1).toFloat(), (y-bw*0.42).toFloat(), (x+bw*0.1).toFloat(), (y+bw*0.42).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.1).toFloat(), (y-bw*0.42).toFloat(), (x+bw*0.1).toFloat(), (y+bw*0.42).toFloat(), stroke)
                c.drawOval((x-bw*0.12).toFloat(), (y-bw*0.12).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.12).toFloat(), fill.apply { color = col })
            }
            "sentinel-nest", "39", "boss39" -> { // #40 SENTINEL-NEST (ID: sentinel-nest)
                c.drawOval((x-bw*0.2).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.2).toFloat(), fill.apply { color = col })
                for (i in 0 until 6) {
                val a = (i * PI / 3).toFloat()
                // px, py = cx + math.cos(a)*s*0.32, cy + math.sin(a)*s*0.32
                c.drawOval((px-10).toFloat(), (py-10).toFloat(), (px+10).toFloat(), (py+10).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((px-10).toFloat(), (py-10).toFloat(), (px+10).toFloat(), (py+10).toFloat(), stroke)
            }
            }
            "dominion", "40", "boss40" -> { // #41 DOMINION (ID: dominion)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.44).toFloat(), (y-bw*0.38).toFloat(), (x+bw*0.44).toFloat(), (y+bw*0.38).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x - bw * 0.15).toFloat(), (y - bw * 0.38).toFloat(), (x - bw * 0.15).toFloat(), (y + bw * 0.38).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x + bw * 0.15).toFloat(), (y - bw * 0.38).toFloat(), (x + bw * 0.15).toFloat(), (y + bw * 0.38).toFloat(), stroke)
                c.drawRect((x-bw*0.15).toFloat(), (y-bw*0.38).toFloat(), (x+bw*0.15).toFloat(), (y+bw*0.38).toFloat(), fill.apply { color = col })
            }
            "sentinel-web", "41", "boss41" -> { // #42 SENTINEL-WEB (ID: sentinel-web)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.4).toFloat(), (y-bw*0.4).toFloat(), (x+bw*0.4).toFloat(), (y+bw*0.4).toFloat(), stroke)
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x + bw * 0.4).toFloat(), (y).toFloat())
                path.lineTo((x).toFloat(), (y + bw * 0.4).toFloat())
                path.lineTo((x - bw * 0.4).toFloat(), (y).toFloat())
                path.close()
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
                c.drawOval((x-bw*0.1).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.1).toFloat(), (y+bw*0.1).toFloat(), fill.apply { color = col })
            }
            "colossus-prime", "42", "boss42" -> { // #43 COLOSSUS-PRIME (ID: colossus-prime)
                c.drawRect(x - bw * 0.45f, y - bw * 0.35f, x + bw * 0.45f, y + bw * 0.1f, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawRect(x - bw * 0.45f, y - bw * 0.35f, x + bw * 0.45f, y + bw * 0.1f, stroke)
                for (k in floatArrayOf(-bw * 0.3f, -bw * 0.1f, bw * 0.1f, bw * 0.3f)) {
                    stroke.color = col; stroke.strokeWidth = 3f
                    c.drawLine(x + k, y + bw * 0.1f, x + k, y + bw * 0.45f, stroke)
                    c.drawRect(x + k - 5f, y + bw * 0.42f, x + k + 5f, y + bw * 0.48f, fill.apply { color = col })
                }
            }
            "spire-guard", "43", "boss43" -> { // #44 SPIRE-GUARD (ID: spire-guard)
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.48).toFloat())
                path.lineTo((x - bw * 0.2).toFloat(), (y + bw * 0.38).toFloat())
                path.lineTo((x + bw * 0.2).toFloat(), (y + bw * 0.38).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                c.drawOval((x-bw*0.35-8).toFloat(), (y-8).toFloat(), (x-bw*0.35+8).toFloat(), (y+8).toFloat(), fill.apply { color = col })
                c.drawOval((x+bw*0.35-8).toFloat(), (y-8).toFloat(), (x+bw*0.35+8).toFloat(), (y+8).toFloat(), fill.apply { color = col })
            }
            "ring-maiden", "44", "boss44" -> { // #45 RING-MAIDEN (ID: ring-maiden)
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawOval(x - bw * 0.25f, y - bw * 0.3f, x + bw * 0.25f, y + bw * 0.2f, stroke)
                c.drawOval(x - bw * 0.08f, y - bw * 0.1f, x + bw * 0.08f, y + bw * 0.06f, fill.apply { color = col })
                for (side in floatArrayOf(-1f, 1f)) {
                    val xa = if (side < 0) x - bw * 0.48f else x + bw * 0.2f
                    val xb = if (side < 0) x - bw * 0.2f else x + bw * 0.48f
                    stroke.color = col; stroke.strokeWidth = 3f
                    c.drawArc(xa, y - bw * 0.1f, xb, y + bw * 0.45f, 0f, 180f, false, stroke)
                }
            }
            "cross-break", "45", "boss45" -> { // #46 CROSS-BREAK (ID: cross-break)
                c.drawRect((x-bw*0.12).toFloat(), (y-bw*0.12).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.12).toFloat(), fill.apply { color = col })
                c.drawRect((x-bw*0.45).toFloat(), (y-bw*0.08).toFloat(), (x-bw*0.2).toFloat(), (y+bw*0.08).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.45).toFloat(), (y-bw*0.08).toFloat(), (x-bw*0.2).toFloat(), (y+bw*0.08).toFloat(), stroke)
                c.drawRect((x+bw*0.2).toFloat(), (y-bw*0.08).toFloat(), (x+bw*0.45).toFloat(), (y+bw*0.08).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x+bw*0.2).toFloat(), (y-bw*0.08).toFloat(), (x+bw*0.45).toFloat(), (y+bw*0.08).toFloat(), stroke)
                c.drawRect((x-bw*0.08).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.08).toFloat(), (y-bw*0.2).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.08).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.08).toFloat(), (y-bw*0.2).toFloat(), stroke)
                c.drawRect((x-bw*0.08).toFloat(), (y+bw*0.2).toFloat(), (x+bw*0.08).toFloat(), (y+bw*0.45).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.08).toFloat(), (y+bw*0.2).toFloat(), (x+bw*0.08).toFloat(), (y+bw*0.45).toFloat(), stroke)
            }
            "nest-guard", "46", "boss46" -> { // #47 NEST-GUARD (ID: nest-guard)
                c.drawOval((x-bw*0.42).toFloat(), (y-bw*0.42).toFloat(), (x+bw*0.42).toFloat(), (y+bw*0.42).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawOval((x-bw*0.42).toFloat(), (y-bw*0.42).toFloat(), (x+bw*0.42).toFloat(), (y+bw*0.42).toFloat(), stroke)
                c.drawOval((x-bw*0.26).toFloat(), (y-bw*0.26).toFloat(), (x+bw*0.26).toFloat(), (y+bw*0.26).toFloat(), fill.apply { color = inv })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.26).toFloat(), (y-bw*0.26).toFloat(), (x+bw*0.26).toFloat(), (y+bw*0.26).toFloat(), stroke)
                c.drawOval((x-bw*0.12).toFloat(), (y-bw*0.12).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.12).toFloat(), fill.apply { color = col })
            }
            "ultimate", "47", "boss47" -> { // #48 ULTIMATE (ID: ultimate)
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.48).toFloat())
                path.lineTo((x + bw * 0.45).toFloat(), (y - bw * 0.1).toFloat())
                path.lineTo((x + bw * 0.35).toFloat(), (y + bw * 0.42).toFloat())
                path.lineTo((x - bw * 0.35).toFloat(), (y + bw * 0.42).toFloat())
                path.lineTo((x - bw * 0.45).toFloat(), (y - bw * 0.1).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                c.drawOval((x-bw*0.22).toFloat(), (y-bw*0.22).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.22).toFloat(), fill.apply { color = inv })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.22).toFloat(), (y-bw*0.22).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.22).toFloat(), stroke)
                c.drawOval((x-bw*0.09).toFloat(), (y-bw*0.09).toFloat(), (x+bw*0.09).toFloat(), (y+bw*0.09).toFloat(), fill.apply { color = col })
            }
            "custom-49", "48", "boss48" -> { // #49 THE DEVEL RED EYE (ID: custom-49)
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.35).toFloat())
                path.lineTo((x - bw * 0.45).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x - bw * 0.25).toFloat(), (y - bw * 0.42).toFloat())
                path.lineTo((x + bw * 0.25).toFloat(), (y - bw * 0.42).toFloat())
                path.lineTo((x + bw * 0.45).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                c.drawOval((x-bw*0.25).toFloat(), (y-bw*0.18).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.18).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.08).toFloat(), (y-bw*0.18).toFloat(), (x+bw*0.08).toFloat(), (y+bw*0.18).toFloat(), fill.apply { color = inv })
                c.drawOval((x-3).toFloat(), (y-3).toFloat(), (x+3).toFloat(), (y+3).toFloat(), fill.apply { color = col })
            }
            "custom-50", "49", "boss49" -> { // #50 UNCENCED AI (ID: custom-50)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.38).toFloat(), (y-bw*0.38).toFloat(), (x+bw*0.38).toFloat(), (y+bw*0.38).toFloat(), stroke)
                c.drawRect((x-bw*0.25).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.25).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.25).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.25).toFloat(), stroke)
                c.drawRect((x-bw*0.12).toFloat(), (y-bw*0.12).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.12).toFloat(), fill.apply { color = col })
                stroke.color = col; stroke.strokeWidth = 3f
                for (cx_sign in floatArrayOf(-1f, 1f)) {
                    for (cy_sign in floatArrayOf(-1f, 1f)) {
                        c.drawLine(x + cx_sign * bw * 0.25f, y + cy_sign * bw * 0.25f, x + cx_sign * bw * 0.45f, y + cy_sign * bw * 0.45f, stroke)
                    }
                }
            }