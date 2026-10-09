            0 -> { // COBRA WYRM: Coiled snake with flared hood, fangs, and segmented tail
                // Hood
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.35).toFloat())
                path.lineTo((x - bw * 0.45).toFloat(), (y - bw * 0.1).toFloat())
                path.lineTo((x - bw * 0.2).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x + bw * 0.2).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x + bw * 0.45).toFloat(), (y - bw * 0.1).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
                // Head & Fangs
                c.drawRect((x-bw*0.12).toFloat(), (y-bw*0.42).toFloat(), (x+bw*0.12).toFloat(), (y-bw*0.15).toFloat(), fill.apply { color = col })
                path.reset()
                path.moveTo((x - bw * 0.1).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x - bw * 0.04).toFloat(), (y + bw * 0.05).toFloat())
                path.lineTo((x).toFloat(), (y - bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                path.reset()
                path.moveTo((x + bw * 0.1).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + bw * 0.04).toFloat(), (y + bw * 0.05).toFloat())
                path.lineTo((x).toFloat(), (y - bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                // Eyes
                c.drawOval((x-bw*0.08).toFloat(), (y-bw*0.32).toFloat(), (x-bw*0.02).toFloat(), (y-bw*0.24).toFloat(), fill.apply { color = inv })
                c.drawOval((x+bw*0.02).toFloat(), (y-bw*0.32).toFloat(), (x+bw*0.08).toFloat(), (y-bw*0.24).toFloat(), fill.apply { color = inv })
                // Tail coils
                for (i in 0 until 4) {
                val ty = (y + bw*0.2 + i * bw*0.1).toFloat()
                val tx = (x + sin(i.toDouble()*1.5)*bw*0.22).toFloat()
                c.drawOval((tx-bw*0.08).toFloat(), (ty-bw*0.08).toFloat(), (tx+bw*0.08).toFloat(), (ty+bw*0.08).toFloat(), fill.apply { color = col })
            }
            1 -> { // CYCLOPS SPIDER: Round hairy thorax, glowing central eye, 8 jointed legs
                c.drawOval((x-bw*0.25).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.25).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.25).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.25).toFloat(), stroke)
                c.drawOval((x-bw*0.12).toFloat(), (y-bw*0.12).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.12).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.04).toFloat(), (y-bw*0.04).toFloat(), (x+bw*0.04).toFloat(), (y+bw*0.04).toFloat(), fill.apply { color = inv })
                for (l in 0 until 4) {
                for (side in floatArrayOf(-1f, 1f)) {
                val x1 = (x + side * bw*0.22).toFloat()
                val y1 = (y - bw*0.18 + l * bw*0.12).toFloat()
                val x2 = (x + side * bw*0.42).toFloat()
                val y2 = (y1 - bw*0.15).toFloat()
                val x3 = (x + side * bw*0.48).toFloat()
                val y3 = (y1 + bw*0.2).toFloat()
                path.reset()
                path.moveTo((x1).toFloat(), (y1).toFloat())
                path.lineTo((x2).toFloat(), (y2).toFloat())
                path.lineTo((x3).toFloat(), (y3).toFloat())
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
            }
            }
            2 -> { // EMPEROR SCORPION: Massive pincers, plated carapace, overhead venom stinger
                // Carapace
                c.drawOval((x-bw*0.2).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.3).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.2).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.3).toFloat(), stroke)
                // Curved tail arching up
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawArc((x-bw*0.25).toFloat(), (y-bw*0.48).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.1).toFloat(), (160).toFloat(), ((380).toFloat() - (160).toFloat()), false, stroke)
                // Stinger bulb
                path.reset()
                path.moveTo((x + bw * 0.15).toFloat(), (y - bw * 0.28).toFloat())
                path.lineTo((x + bw * 0.28).toFloat(), (y - bw * 0.42).toFloat())
                path.lineTo((x + bw * 0.05).toFloat(), (y - bw * 0.4).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                // Dual front claws
                for (side in floatArrayOf(-1f, 1f)) {
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x + side * bw * 0.15).toFloat(), (y).toFloat(), (x + side * bw * 0.35).toFloat(), (y - bw * 0.15).toFloat(), stroke)
                path.reset()
                path.moveTo((x + side * bw * 0.35).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + side * bw * 0.46).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x + side * bw * 0.25).toFloat(), (y - bw * 0.32).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            3 -> { // TITAN CENTIPEDE: 7 overlapping body rings with crawling side spurs
                for (i in 0 until 7) {
                val segY = (y - bw*0.4 + i * bw*0.13).toFloat()
                c.drawOval((x-bw*0.22).toFloat(), (y-bw*0.08).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.08).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.22).toFloat(), (y-bw*0.08).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.08).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x - bw * 0.22).toFloat(), (y).toFloat(), (x - bw * 0.4).toFloat(), (y - bw * 0.06).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x + bw * 0.22).toFloat(), (y).toFloat(), (x + bw * 0.4).toFloat(), (y - bw * 0.06).toFloat(), stroke)
                // Head mandibles
                path.reset()
                path.moveTo((x - bw * 0.12).toFloat(), (y - bw * 0.42).toFloat())
                path.lineTo((x - bw * 0.2).toFloat(), (y - bw * 0.52).toFloat())
                path.lineTo((x - bw * 0.04).toFloat(), (y - bw * 0.48).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                path.reset()
                path.moveTo((x + bw * 0.12).toFloat(), (y - bw * 0.42).toFloat())
                path.lineTo((x + bw * 0.2).toFloat(), (y - bw * 0.52).toFloat())
                path.lineTo((x + bw * 0.04).toFloat(), (y - bw * 0.48).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            4 -> { // GIANT CRAB: Broad armored shell, twin eye stalks, serrated crushing claws
                c.drawOval((x-bw*0.35).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.25).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.35).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.25).toFloat(), stroke)
                // Eye stalks
                for (side in floatArrayOf(-1f, 1f)) {
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x + side * bw * 0.1).toFloat(), (y - bw * 0.18).toFloat(), (x + side * bw * 0.14).toFloat(), (y - bw * 0.35).toFloat(), stroke)
                c.drawOval((x+side*bw*0.14-4).toFloat(), (y-bw*0.35-4).toFloat(), (x+side*bw*0.14+4).toFloat(), (y-bw*0.35+4).toFloat(), fill.apply { color = col })
                // Claws
                for (side in floatArrayOf(-1f, 1f)) {
                c.drawRect((x+side*bw*0.38-bw*0.08).toFloat(), (y-bw*0.25).toFloat(), (x+side*bw*0.38+bw*0.08).toFloat(), (y+bw*0.05).toFloat(), fill.apply { color = col })
                path.reset()
                path.moveTo((x + side * bw * 0.38).toFloat(), (y - bw * 0.25).toFloat())
                path.lineTo((x + side * bw * 0.48).toFloat(), (y - bw * 0.42).toFloat())
                path.lineTo((x + side * bw * 0.3).toFloat(), (y - bw * 0.35).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            }
            5 -> { // ABYSSAL KRAKEN: Central bulbous mantle with 6 undulating tentacles
                c.drawOval((x-bw*0.28).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.28).toFloat(), (y).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.28).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.28).toFloat(), (y).toFloat(), stroke)
                c.drawOval((x-bw*0.1).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.1).toFloat(), (y-bw*0.1).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.04).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.04).toFloat(), (y-bw*0.15).toFloat(), fill.apply { color = inv })
                // Tentacles hanging below
                for (i in 0 until 6) {
                val tx = (x - bw*0.22 + i * bw*0.09).toFloat()
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawArc((tx-bw*0.08).toFloat(), (y).toFloat(), (tx+bw*0.08).toFloat(), (y+bw*0.45).toFloat(), (0).toFloat(), ((180).toFloat() - (0).toFloat()), false, stroke)
            }
            6 -> { // DRAGON FLY / MANTIS: Triangular head, razor forearms, double cross wings
                // Head & Body
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.38).toFloat())
                path.lineTo((x - bw * 0.12).toFloat(), (y - bw * 0.18).toFloat())
                path.lineTo((x + bw * 0.12).toFloat(), (y - bw * 0.18).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                c.drawRect((x-bw*0.06).toFloat(), (y-bw*0.18).toFloat(), (x+bw*0.06).toFloat(), (y+bw*0.4).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.06).toFloat(), (y-bw*0.18).toFloat(), (x+bw*0.06).toFloat(), (y+bw*0.4).toFloat(), stroke)
                // Forearms (scythes)
                for (side in floatArrayOf(-1f, 1f)) {
                path.reset()
                path.moveTo((x + side * bw * 0.1).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + side * bw * 0.35).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x + side * bw * 0.22).toFloat(), (y).toFloat())
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                // Wings
                for (side in floatArrayOf(-1f, 1f)) {
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.1).toFloat())
                path.lineTo((x + side * bw * 0.46).toFloat(), (y - bw * 0.22).toFloat())
                path.lineTo((x + side * bw * 0.4).toFloat(), (y - bw * 0.05).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.05).toFloat())
                path.lineTo((x + side * bw * 0.42).toFloat(), (y + bw * 0.02).toFloat())
                path.lineTo((x + side * bw * 0.36).toFloat(), (y + bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            }
            7 -> { // BIO-HYDRA: 3 distinct dragon/serpent heads emerging from one trunk
                // Base trunk
                c.drawRect((x-bw*0.2).toFloat(), (y+bw*0.15).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.42).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.2).toFloat(), (y+bw*0.15).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.42).toFloat(), stroke)
                // 3 necks & heads
                // for idx, ang in enumerate([-0.5, 0, 0.5]):
                val hx = (x + sin(ang) * bw*0.32).toFloat()
                val hy = (y - bw*0.15 - (0.1 if idx == 1 else 0)).toFloat()
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x).toFloat(), (y + bw * 0.2).toFloat(), (hx).toFloat(), (hy).toFloat(), stroke)
                path.reset()
                path.moveTo((hx).toFloat(), (hy - bw * 0.15).toFloat())
                path.lineTo((hx - bw * 0.1).toFloat(), (hy + bw * 0.05).toFloat())
                path.lineTo((hx + bw * 0.1).toFloat(), (hy + bw * 0.05).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                c.drawOval((hx-3).toFloat(), (hy-bw*0.05-3).toFloat(), (hx+3).toFloat(), (hy-bw*0.05+3).toFloat(), fill.apply { color = inv })
            }
            8 -> { // MECHA-BEETLE (Scarab): Heavy domed shell, segmented pincer horns, shell lines
                c.drawOval((x-bw*0.3).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.3).toFloat(), (y+bw*0.35).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.3).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.3).toFloat(), (y+bw*0.35).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x).toFloat(), (y - bw * 0.15).toFloat(), (x).toFloat(), (y + bw * 0.35).toFloat(), stroke)
                // Heavy front horn
                c.drawRect((x-bw*0.08).toFloat(), (y-bw*0.35).toFloat(), (x+bw*0.08).toFloat(), (y-bw*0.15).toFloat(), fill.apply { color = col })
                path.reset()
                path.moveTo((x - bw * 0.15).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x - bw * 0.05).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x + bw * 0.05).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x + bw * 0.15).toFloat(), (y - bw * 0.45).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            9 -> { // JELLYFISH LEVIATHAN: Glowing bell dome with trailing shock tentacle tendrils
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawArc((x - bw * 0.35f), (y - bw * 0.4f), (x + bw * 0.35f), (y + bw * 0.1f), 180f, 180f, true, fill.apply { color = bg })
                c.drawArc((x - bw * 0.35f), (y - bw * 0.4f), (x + bw * 0.35f), (y + bw * 0.1f), 180f, 180f, false, stroke)
                c.drawOval((x - bw * 0.15f), (y - bw * 0.3f), (x + bw * 0.15f), (y - bw * 0.1f), fill.apply { color = col })
                // Tendrils
                for (i in 0 until 5) {
                    val tx = (x - bw * 0.25f + i * bw * 0.125f)
                    stroke.color = col; stroke.strokeWidth = 2f
                    c.drawLine(tx, y - bw * 0.15f, (tx + sin(i.toDouble()) * 10f).toFloat(), y + bw * 0.4f, stroke)
                }
            }
            10 -> { // SHADOW BAT: Giant horned ears, spread jagged wings, glowing fangs
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.25).toFloat())
                path.lineTo((x - bw * 0.48).toFloat(), (y - bw * 0.3).toFloat())
                path.lineTo((x - bw * 0.3).toFloat(), (y - bw * 0.05).toFloat())
                path.lineTo((x - bw * 0.15).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + bw * 0.15).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x + bw * 0.3).toFloat(), (y - bw * 0.05).toFloat())
                path.lineTo((x + bw * 0.48).toFloat(), (y - bw * 0.3).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
                // Ears
                path.reset()
                path.moveTo((x - bw * 0.12).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x - bw * 0.08).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x - bw * 0.02).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                path.reset()
                path.moveTo((x + bw * 0.12).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x + bw * 0.08).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x + bw * 0.02).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                c.drawOval((x-4).toFloat(), (y-bw*0.05-4).toFloat(), (x+4).toFloat(), (y-bw*0.05+4).toFloat(), fill.apply { color = col })
            }
            11 -> { // OROBOROS (Ring Snake swallowing tail): Circular segmented serpent
                val r = (bw * 0.35).toFloat()
                stroke.color = col; stroke.strokeWidth = 8f
                c.drawOval((x-r).toFloat(), (y-r).toFloat(), (x+r).toFloat(), (y+r).toFloat(), stroke)
                for (i in 0 until 8) {
                val a = (i * PI / 4).toFloat()
                c.drawOval((x+cos(a)*r-6).toFloat(), (y+sin(a)*r-6).toFloat(), (x+cos(a)*r+6).toFloat(), (y+sin(a)*r+6).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x+cos(a)*r-6).toFloat(), (y+sin(a)*r-6).toFloat(), (x+cos(a)*r+6).toFloat(), (y+sin(a)*r+6).toFloat(), stroke)
                // Dragon head biting tail
                path.reset()
                path.moveTo((x + r).toFloat(), (y - bw * 0.1).toFloat())
                path.lineTo((x + r + bw * 0.15).toFloat(), (y).toFloat())
                path.lineTo((x + r - bw * 0.05).toFloat(), (y + bw * 0.12).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            12 -> { // GARGOYLE GOLEM: Broad shoulder blocks, horned crest, heavy center chest eye
                c.drawRect((x-bw*0.35).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.25).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.35).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.25).toFloat(), stroke)
                // Horns
                path.reset()
                path.moveTo((x - bw * 0.25).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x - bw * 0.35).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x - bw * 0.15).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                path.reset()
                path.moveTo((x + bw * 0.25).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x + bw * 0.35).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x + bw * 0.15).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                // Core
                c.drawOval((x-bw*0.12).toFloat(), (y-bw*0.05).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.19).toFloat(), fill.apply { color = col })
                c.drawRect((x-3).toFloat(), (y+bw*0.02).toFloat(), (x+3).toFloat(), (y+bw*0.12).toFloat(), fill.apply { color = inv })
            }
            13 -> { // ANGLER LEVIATHAN: Huge gaping jaw, luminous dorsal lure hanging over mouth
                c.drawOval((x-bw*0.32).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.32).toFloat(), (y+bw*0.3).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.32).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.32).toFloat(), (y+bw*0.3).toFloat(), stroke)
                // Gaping mouth
                path.reset()
                path.moveTo((x - bw * 0.2).toFloat(), (y + bw * 0.05).toFloat())
                path.lineTo((x + bw * 0.2).toFloat(), (y + bw * 0.05).toFloat())
                path.lineTo((x).toFloat(), (y + bw * 0.28).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = inv })
                // Teeth
                for (i in floatArrayOf(-bw*0.12f, -s*0.04f, bw*0.04f, bw*0.12f)) {
                path.reset()
                path.moveTo((x + i - 2).toFloat(), (y + bw * 0.05).toFloat())
                path.lineTo((x + i).toFloat(), (y + bw * 0.12).toFloat())
                path.lineTo((x + i + 2).toFloat(), (y + bw * 0.05).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                // Lure stalk
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawArc((x-bw*0.15).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.15).toFloat(), (y-bw*0.1).toFloat(), (180).toFloat(), ((360).toFloat() - (180).toFloat()), false, stroke)
                c.drawOval((x+bw*0.15-6).toFloat(), (y-bw*0.2-6).toFloat(), (x+bw*0.15+6).toFloat(), (y-bw*0.2+6).toFloat(), fill.apply { color = col })
            }
            14 -> { // CENTAUR BEAST: Forearm blade armor with quadruped lower mechanical body
                c.drawRect((x-bw*0.15).toFloat(), (y-bw*0.38).toFloat(), (x+bw*0.15).toFloat(), (y).toFloat(), fill.apply { color = col })
                c.drawRect((x-bw*0.35).toFloat(), (y).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.25).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.35).toFloat(), (y).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.25).toFloat(), stroke)
                // Legs
                for (side in floatArrayOf(-1f, 1f)) {
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x + side * bw * 0.25).toFloat(), (y + bw * 0.25).toFloat(), (x + side * bw * 0.35).toFloat(), (y + bw * 0.45).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x + side * bw * 0.1).toFloat(), (y + bw * 0.25).toFloat(), (x + side * bw * 0.15).toFloat(), (y + bw * 0.45).toFloat(), stroke)
            }
            15 -> { // CHIMERA WYRM: Dual serpent bodies joined at a central armored skull
                c.drawOval((x-bw*0.18).toFloat(), (y-bw*0.18).toFloat(), (x+bw*0.18).toFloat(), (y+bw*0.18).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.06).toFloat(), (y-bw*0.06).toFloat(), (x+bw*0.06).toFloat(), (y+bw*0.06).toFloat(), fill.apply { color = inv })
                for (side in floatArrayOf(-1f, 1f)) {
                for (i in 0 until 4) {
                val sx = (x + side * (bw*0.15 + i * bw*0.09)).toFloat()
                val sy = (y + sin(i.toDouble()*1.2)*bw*0.18).toFloat()
                c.drawOval((sx-7).toFloat(), (sy-7).toFloat(), (sx+7).toFloat(), (sy+7).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((sx-7).toFloat(), (sy-7).toFloat(), (sx+7).toFloat(), (sy+7).toFloat(), stroke)
            }
            }
            16 -> { // ARMORED WASP: Narrow waist, heavy stinger needle, angled insectoid wings
                c.drawOval((x-bw*0.15).toFloat(), (y-bw*0.35).toFloat(), (x+bw*0.15).toFloat(), (y-bw*0.1).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.15).toFloat(), (y-bw*0.35).toFloat(), (x+bw*0.15).toFloat(), (y-bw*0.1).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawLine((x).toFloat(), (y - bw * 0.1).toFloat(), (x).toFloat(), (y + bw * 0.05).toFloat(), stroke)
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.42).toFloat())
                path.lineTo((x - bw * 0.18).toFloat(), (y + bw * 0.05).toFloat())
                path.lineTo((x + bw * 0.18).toFloat(), (y + bw * 0.05).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                for (side in floatArrayOf(-1f, 1f)) {
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.12).toFloat())
                path.lineTo((x + side * bw * 0.44).toFloat(), (y - bw * 0.3).toFloat())
                path.lineTo((x + side * bw * 0.35).toFloat(), (y - bw * 0.05).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
            }
            17 -> { // CYBER GHOUL: Skull-shaped mask with glowing eye sockets and ribcage frame
                c.drawOval((x-bw*0.25).toFloat(), (y-bw*0.35).toFloat(), (x+bw*0.25).toFloat(), (y).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.14).toFloat(), (y-bw*0.22).toFloat(), (x-bw*0.04).toFloat(), (y-bw*0.12).toFloat(), fill.apply { color = inv })
                c.drawOval((x+bw*0.04).toFloat(), (y-bw*0.22).toFloat(), (x+bw*0.14).toFloat(), (y-bw*0.12).toFloat(), fill.apply { color = inv })
                // Ribcage
                for (i in 0 until 3) {
                val ry = (y + bw*0.08 + i * bw*0.1).toFloat()
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x - bw * 0.22).toFloat(), (ry).toFloat(), (x + bw * 0.22).toFloat(), (ry).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x).toFloat(), (y).toFloat(), (x).toFloat(), (y + bw * 0.38).toFloat(), stroke)
            }
            18 -> { // TRICERATOPS RAM: Heavy triangular frill shield with 3 massive forward horns
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x - bw * 0.4).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x + bw * 0.4).toFloat(), (y - bw * 0.4).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                c.drawRect((x-bw*0.18).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.18).toFloat(), (y+bw*0.25).toFloat(), fill.apply { color = col })
                // 3 horns pointing down
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.25).toFloat())
                path.lineTo((x - bw * 0.05).toFloat(), (y + bw * 0.45).toFloat())
                path.lineTo((x + bw * 0.05).toFloat(), (y + bw * 0.45).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                path.reset()
                path.moveTo((x - bw * 0.28).toFloat(), (y - bw * 0.3).toFloat())
                path.lineTo((x - bw * 0.38).toFloat(), (y).toFloat())
                path.lineTo((x - bw * 0.2).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                path.reset()
                path.moveTo((x + bw * 0.28).toFloat(), (y - bw * 0.3).toFloat())
                path.lineTo((x + bw * 0.38).toFloat(), (y).toFloat())
                path.lineTo((x + bw * 0.2).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            19 -> { // VOID BASILISK: Multi-eyed lizard head with crown crest and coiled serpent tail
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.15).toFloat())
                path.lineTo((x - bw * 0.25).toFloat(), (y - bw * 0.25).toFloat())
                path.lineTo((x + bw * 0.25).toFloat(), (y - bw * 0.25).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                // Crown spikes
                for (i in floatArrayOf(-0.2f, 0f, 0.2f)) {
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x + i * bw).toFloat(), (y - bw * 0.25).toFloat(), (x + i * bw * 1.3).toFloat(), (y - bw * 0.45).toFloat(), stroke)
                // Snake tail
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawArc((x-bw*0.25).toFloat(), (y+bw*0.1).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.45).toFloat(), (0).toFloat(), ((180).toFloat() - (0).toFloat()), false, stroke)
                c.drawOval((x-4).toFloat(), (y-4).toFloat(), (x+4).toFloat(), (y+4).toFloat(), fill.apply { color = col })
            }
            20 -> { // EYE CLUSTER OVERMIND: 1 primary eye surrounded by 6 orbiting mini-eyes
                c.drawOval((x-bw*0.2).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.2).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.08).toFloat(), (y-bw*0.08).toFloat(), (x+bw*0.08).toFloat(), (y+bw*0.08).toFloat(), fill.apply { color = inv })
                for (i in 0 until 6) {
                val a = (i * PI / 3).toFloat()
                val ox = (x + cos(a)*bw*0.35).toFloat()
                val oy = (y + sin(a)*bw*0.35).toFloat()
                c.drawOval((ox-8).toFloat(), (oy-8).toFloat(), (ox+8).toFloat(), (oy+8).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((ox-8).toFloat(), (oy-8).toFloat(), (ox+8).toFloat(), (oy+8).toFloat(), stroke)
                c.drawOval((ox-2).toFloat(), (oy-2).toFloat(), (ox+2).toFloat(), (oy+2).toFloat(), fill.apply { color = col })
            }
            21 -> { // GARGANTUAN BEHEMOTH: Mammoth armor shoulders with hanging tusk spikes
                c.drawRect((x-bw*0.45).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.45).toFloat(), (y+bw*0.1).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.45).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.45).toFloat(), (y+bw*0.1).toFloat(), stroke)
                // Center visor slit
                c.drawRect((x-bw*0.25).toFloat(), (y-bw*0.05).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.05).toFloat(), fill.apply { color = col })
                // Massive downward curved tusks
                path.reset()
                path.moveTo((x - bw * 0.35).toFloat(), (y + bw * 0.1).toFloat())
                path.lineTo((x - bw * 0.45).toFloat(), (y + bw * 0.45).toFloat())
                path.lineTo((x - bw * 0.25).toFloat(), (y + bw * 0.25).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                path.reset()
                path.moveTo((x + bw * 0.35).toFloat(), (y + bw * 0.1).toFloat())
                path.lineTo((x + bw * 0.45).toFloat(), (y + bw * 0.45).toFloat())
                path.lineTo((x + bw * 0.25).toFloat(), (y + bw * 0.25).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            22 -> { // BIO-NAUTILUS: Spiral shell with trailing tentacles
                c.drawOval((x-bw*0.35).toFloat(), (y-bw*0.38).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.15).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.35).toFloat(), (y-bw*0.38).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.15).toFloat(), stroke)
                c.drawOval((x-bw*0.2).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.05).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.08).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.08).toFloat(), (y).toFloat(), fill.apply { color = inv })
                for (i in 0 until 5) {
                val tx = (x - bw*0.2 + i * bw*0.1).toFloat()
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((tx).toFloat(), (y + bw * 0.1).toFloat(), (tx + sin(i.toDouble()) * 8).toFloat(), (y + bw * 0.45).toFloat(), stroke)
            }
            23 -> { // PHOENIX (Firebird): Upward flame wings with long feathered tail streamers
                // Head & beak
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x - bw * 0.08).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x + bw * 0.08).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                // Wings
                for (side in floatArrayOf(-1f, 1f)) {
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x + side * bw * 0.45).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x + side * bw * 0.35).toFloat(), (y - bw * 0.05).toFloat())
                path.lineTo((x + side * bw * 0.48).toFloat(), (y + bw * 0.15).toFloat())
                path.lineTo((x).toFloat(), (y + bw * 0.1).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
                // Tail streamers
                for (side in floatArrayOf(-0.08f, 0f, 0.08f)) {
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x + side * bw).toFloat(), (y + bw * 0.1).toFloat(), (x + side * bw * 2).toFloat(), (y + bw * 0.48).toFloat(), stroke)
            }
            }
            24 -> { // CYBER-LEECH: Tube mouth with concentric teeth rings and pulsing bulb tail
                c.drawOval((x-bw*0.25).toFloat(), (y-bw*0.42).toFloat(), (x+bw*0.25).toFloat(), (y-bw*0.15).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.14).toFloat(), (y-bw*0.36).toFloat(), (x+bw*0.14).toFloat(), (y-bw*0.21).toFloat(), fill.apply { color = inv })
                c.drawOval((x-bw*0.05).toFloat(), (y-bw*0.31).toFloat(), (x+bw*0.05).toFloat(), (y-bw*0.26).toFloat(), fill.apply { color = col })
                // Body sacks
                c.drawOval((x-bw*0.2).toFloat(), (y-bw*0.18).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.15).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.2).toFloat(), (y-bw*0.18).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.15).toFloat(), stroke)
                c.drawOval((x-bw*0.15).toFloat(), (y+bw*0.12).toFloat(), (x+bw*0.15).toFloat(), (y+bw*0.42).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.15).toFloat(), (y+bw*0.12).toFloat(), (x+bw*0.15).toFloat(), (y+bw*0.42).toFloat(), stroke)
            }
            25 -> { // ANUBIS MECH: Jackal head with upright pointed ears and glowing collar
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.1).toFloat())
                path.lineTo((x - bw * 0.15).toFloat(), (y - bw * 0.18).toFloat())
                path.lineTo((x + bw * 0.15).toFloat(), (y - bw * 0.18).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                // Tall ears
                path.reset()
                path.moveTo((x - bw * 0.15).toFloat(), (y - bw * 0.18).toFloat())
                path.lineTo((x - bw * 0.18).toFloat(), (y - bw * 0.48).toFloat())
                path.lineTo((x - bw * 0.06).toFloat(), (y - bw * 0.18).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                path.reset()
                path.moveTo((x + bw * 0.15).toFloat(), (y - bw * 0.18).toFloat())
                path.lineTo((x + bw * 0.18).toFloat(), (y - bw * 0.48).toFloat())
                path.lineTo((x + bw * 0.06).toFloat(), (y - bw * 0.18).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                // Collar mantle
                path.reset()
                path.moveTo((x - bw * 0.35).toFloat(), (y + bw * 0.1).toFloat())
                path.lineTo((x + bw * 0.35).toFloat(), (y + bw * 0.1).toFloat())
                path.lineTo((x).toFloat(), (y + bw * 0.38).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
            }
            26 -> { // ARMORED RAY: Diamond ray wings with long whip stinger tail
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x + bw * 0.46).toFloat(), (y).toFloat())
                path.lineTo((x).toFloat(), (y + bw * 0.25).toFloat())
                path.lineTo((x - bw * 0.46).toFloat(), (y).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x).toFloat(), (y + bw * 0.25).toFloat(), (x).toFloat(), (y + bw * 0.48).toFloat(), stroke)
                c.drawOval((x-6).toFloat(), (y-bw*0.1-6).toFloat(), (x+6).toFloat(), (y-bw*0.1+6).toFloat(), fill.apply { color = col })
            }
            27 -> { // GORGON (Medusa): Face core with 6 slithering snake hair tendrils
                c.drawOval((x-bw*0.18).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.18).toFloat(), (y+bw*0.22).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.08).toFloat(), (y).toFloat(), (x-bw*0.02).toFloat(), (y+bw*0.06).toFloat(), fill.apply { color = inv })
                c.drawOval((x+bw*0.02).toFloat(), (y).toFloat(), (x+bw*0.08).toFloat(), (y+bw*0.06).toFloat(), fill.apply { color = inv })
                // Snake hair
                for (a in 0 until 6) {
                val ang = (a * PI / 5 - PI).toFloat()
                val x1 = (x + cos(ang) * bw*0.18).toFloat()
                val y1 = (y - bw*0.15 + sin(ang) * bw*0.15).toFloat()
                val x2 = (x + cos(ang) * bw*0.42).toFloat()
                val y2 = (y - bw*0.25 + sin(ang) * bw*0.25).toFloat()
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x1).toFloat(), (y1).toFloat(), (x2).toFloat(), (y2).toFloat(), stroke)
                c.drawOval((x2-4).toFloat(), (y2-4).toFloat(), (x2+4).toFloat(), (y2+4).toFloat(), fill.apply { color = col })
            }
            28 -> { // CARRIER BEETLE: Heavy split wing covers exposing glowing pulsating engine
                // Exposed inner core
                c.drawOval((x-bw*0.15).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.15).toFloat(), (y+bw*0.2).toFloat(), fill.apply { color = col })
                // Left and right wing shells open
                path.reset()
                path.moveTo((x - bw * 0.05).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x - bw * 0.42).toFloat(), (y - bw * 0.05).toFloat())
                path.lineTo((x - bw * 0.35).toFloat(), (y + bw * 0.35).toFloat())
                path.lineTo((x - bw * 0.05).toFloat(), (y + bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
                path.reset()
                path.moveTo((x + bw * 0.05).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x + bw * 0.42).toFloat(), (y - bw * 0.05).toFloat())
                path.lineTo((x + bw * 0.35).toFloat(), (y + bw * 0.35).toFloat())
                path.lineTo((x + bw * 0.05).toFloat(), (y + bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
            }
            29 -> { // TITAN CRUSTACEAN: Multi-segmented carapace with front crusher claws
                for (i in 0 until 4) {
                val w = (bw * (0.35 - i * 0.05)).toFloat()
                val segY = (y - bw*0.15 + i * bw*0.12).toFloat()
                c.drawRect((x-w).toFloat(), (y).toFloat(), (x+w).toFloat(), (y+bw*0.09).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-w).toFloat(), (y).toFloat(), (x+w).toFloat(), (y+bw*0.09).toFloat(), stroke)
                // Claws
                for (side in floatArrayOf(-1f, 1f)) {
                path.reset()
                path.moveTo((x + side * bw * 0.2).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + side * bw * 0.42).toFloat(), (y - bw * 0.38).toFloat())
                path.lineTo((x + side * bw * 0.3).toFloat(), (y - bw * 0.42).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            }
            30 -> { // MECHANICAL FLY: Giant faceted compound eyes with high-speed vibrating wings
                c.drawOval((x-bw*0.12).toFloat(), (y-bw*0.05).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.3).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.12).toFloat(), (y-bw*0.05).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.3).toFloat(), stroke)
                // Giant twin compound eyes
                c.drawOval((x-bw*0.28).toFloat(), (y-bw*0.3).toFloat(), (x-bw*0.04).toFloat(), (y-bw*0.06).toFloat(), fill.apply { color = col })
                c.drawOval((x+bw*0.04).toFloat(), (y-bw*0.3).toFloat(), (x+bw*0.28).toFloat(), (y-bw*0.06).toFloat(), fill.apply { color = col })
                for (side in floatArrayOf(-1f, 1f)) {
                c.drawOval((x+side*bw*0.28-bw*0.15).toFloat(), (y-bw*0.1).toFloat(), (x+side*bw*0.28+bw*0.15).toFloat(), (y+bw*0.1).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x+side*bw*0.28-bw*0.15).toFloat(), (y-bw*0.1).toFloat(), (x+side*bw*0.28+bw*0.15).toFloat(), (y+bw*0.1).toFloat(), stroke)
            }
            31 -> { // DEVOURER MOUTH: Giant circular toothy vortex with outer armor ring
                c.drawOval((x-bw*0.38).toFloat(), (y-bw*0.38).toFloat(), (x+bw*0.38).toFloat(), (y+bw*0.38).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawOval((x-bw*0.38).toFloat(), (y-bw*0.38).toFloat(), (x+bw*0.38).toFloat(), (y+bw*0.38).toFloat(), stroke)
                c.drawOval((x-bw*0.2).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.2).toFloat(), fill.apply { color = inv })
                for (i in 0 until 8) {
                val a = (i * PI / 4).toFloat()
                val tx = (x + cos(a)*bw*0.2).toFloat()
                val ty = (y + sin(a)*bw*0.2).toFloat()
                path.reset()
                path.moveTo((tx).toFloat(), (ty).toFloat())
                path.lineTo((tx + cos(a + 0.3) * 12).toFloat(), (ty + sin(a + 0.3) * 12).toFloat())
                path.lineTo((x).toFloat(), (y).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            32 -> { // GHOST WYRM: Skeletal floating serpent ribs with glowing skull
                // Skull
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x - bw * 0.15).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x + bw * 0.15).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                c.drawOval((x-4).toFloat(), (y-bw*0.28-4).toFloat(), (x+4).toFloat(), (y-bw*0.28+4).toFloat(), fill.apply { color = inv })
                // Rib cages
                for (i in 0 until 5) {
                val segY = (y - bw*0.1 + i * bw*0.1).toFloat()
                val w = (bw * (0.35 - i * 0.04)).toFloat()
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawArc((x-w).toFloat(), (y-bw*0.05).toFloat(), (x+w).toFloat(), (y+bw*0.08).toFloat(), (0).toFloat(), ((180).toFloat() - (0).toFloat()), false, stroke)
            }
            33 -> { // DRAGON CHIEF: Horned draconian skull, wide scaled crest, fire organ
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.25).toFloat())
                path.lineTo((x - bw * 0.2).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + bw * 0.2).toFloat(), (y - bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                // Long swept backward horns
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawLine((x - bw * 0.15).toFloat(), (y - bw * 0.15).toFloat(), (x - bw * 0.4).toFloat(), (y - bw * 0.45).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawLine((x + bw * 0.15).toFloat(), (y - bw * 0.15).toFloat(), (x + bw * 0.4).toFloat(), (y - bw * 0.45).toFloat(), stroke)
                // Glowing mouth
                c.drawOval((x-bw*0.08).toFloat(), (y+bw*0.05).toFloat(), (x+bw*0.08).toFloat(), (y+bw*0.2).toFloat(), fill.apply { color = col })
            }
            34 -> { // TRIPLE CANNON CRAB: Broad carapace with 3 massive forward railgun barrels
                c.drawRect((x-bw*0.4).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.4).toFloat(), (y+bw*0.25).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.4).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.4).toFloat(), (y+bw*0.25).toFloat(), stroke)
                // 3 cannon barrels pointing down
                for (k in floatArrayOf(-bw*0.25f, 0f, bw*0.25f)) {
                c.drawRect((x+k-bw*0.05).toFloat(), (y+bw*0.25).toFloat(), (x+k+bw*0.05).toFloat(), (y+bw*0.45).toFloat(), fill.apply { color = col })
                // Eyes
                c.drawOval((x-bw*0.15).toFloat(), (y).toFloat(), (x-bw*0.05).toFloat(), (y+bw*0.1).toFloat(), fill.apply { color = col })
                c.drawOval((x+bw*0.05).toFloat(), (y).toFloat(), (x+bw*0.15).toFloat(), (y+bw*0.1).toFloat(), fill.apply { color = col })
            }
            35 -> { // SEA SERPENT: Undulating S-curve snake with dorsal fin spines
                var lastX = x
                var lastY = y - bw * 0.4f
                val headX = (x + sin(0.0) * bw * 0.3f).toFloat()
                val headY = (y - bw * 0.4f)
                c.drawOval(headX - 8f, headY - 8f, headX + 8f, headY + 8f, fill.apply { color = col })
                for (i in 0 until 12) {
                    val t = i / 11.0
                    val curX = (x + sin(t * PI * 2.0) * bw * 0.3f).toFloat()
                    val curY = (y - bw * 0.4f + t * bw * 0.85f).toFloat()
                    if (i > 0) {
                        stroke.color = col; stroke.strokeWidth = 6f
                        c.drawLine(lastX, lastY, curX, curY, stroke)
                    }
                    if (i % 2 == 0) {
                        stroke.color = col; stroke.strokeWidth = 2f
                        c.drawLine(curX, curY, curX + 15f, curY - 10f, stroke)
                    }
                    lastX = curX
                    lastY = curY
                }
            36 -> { // QUEEN WASP: Giant egg sack abdomen with razor wing blades
                // Giant egg sack
                c.drawOval((x-bw*0.22).toFloat(), (y).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.45).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.22).toFloat(), (y).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.45).toFloat(), stroke)
                // Thorax & head
                c.drawOval((x-bw*0.15).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.15).toFloat(), (y).toFloat(), fill.apply { color = col })
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.42).toFloat())
                path.lineTo((x - bw * 0.1).toFloat(), (y - bw * 0.25).toFloat())
                path.lineTo((x + bw * 0.1).toFloat(), (y - bw * 0.25).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                for (side in floatArrayOf(-1f, 1f)) {
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x).toFloat(), (y - bw * 0.15).toFloat(), (x + side * bw * 0.45).toFloat(), (y - bw * 0.3).toFloat(), stroke)
            }
            37 -> { // COCKATRICE: Avian razor beak, bat wings, long coiled serpent tail
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x - bw * 0.15).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + bw * 0.15).toFloat(), (y - bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                c.drawRect((x-bw*0.2).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.15).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.2).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.15).toFloat(), stroke)
                // Snake tail
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawArc((x-bw*0.2).toFloat(), (y+bw*0.1).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.45).toFloat(), (0).toFloat(), ((270).toFloat() - (0).toFloat()), false, stroke)
                for (side in floatArrayOf(-1f, 1f)) {
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.05).toFloat())
                path.lineTo((x + side * bw * 0.42).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x + side * bw * 0.35).toFloat(), (y + bw * 0.05).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            38 -> { // SOLAR SCARAB: Sacred scarab pushing a giant glowing sun sphere
                // Sun sphere
                c.drawOval((x-bw*0.2).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.2).toFloat(), (y-bw*0.05).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.1).toFloat(), (y-bw*0.35).toFloat(), (x+bw*0.1).toFloat(), (y-bw*0.15).toFloat(), fill.apply { color = inv })
                // Scarab body
                c.drawOval((x-bw*0.28).toFloat(), (y-bw*0.05).toFloat(), (x+bw*0.28).toFloat(), (y+bw*0.4).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.28).toFloat(), (y-bw*0.05).toFloat(), (x+bw*0.28).toFloat(), (y+bw*0.4).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x).toFloat(), (y - bw * 0.05).toFloat(), (x).toFloat(), (y + bw * 0.4).toFloat(), stroke)
            }
            39 -> { // MANTA LEVIATHAN: Curved sweeping delta fins with central glowing bioluminescent eye
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.3).toFloat())
                path.lineTo((x - bw * 0.48).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x - bw * 0.25).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + bw * 0.25).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x + bw * 0.48).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                c.drawOval((x-bw*0.1).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.1).toFloat(), (y+bw*0.1).toFloat(), fill.apply { color = col })
                c.drawOval((x-3).toFloat(), (y-3).toFloat(), (x+3).toFloat(), (y+3).toFloat(), fill.apply { color = inv })
            }
            40 -> { // HYPER SPIDER (8-eyed web mistress): Huge arachnid with web net lines
                c.drawOval((x-bw*0.22).toFloat(), (y-bw*0.22).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.22).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.22).toFloat(), (y-bw*0.22).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.22).toFloat(), stroke)
                // 8 mini eyes
                for (i in 0 until 8) {
                val a = (i * PI / 4).toFloat()
                c.drawOval((x+cos(a)*bw*0.12-2).toFloat(), (y+sin(a)*bw*0.12-2).toFloat(), (x+cos(a)*bw*0.12+2).toFloat(), (y+sin(a)*bw*0.12+2).toFloat(), fill.apply { color = col })
                // Long spidery legs
                for (i in 0 until 4) {
                for (side in floatArrayOf(-1f, 1f)) {
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x + side * bw * 0.2).toFloat(), (y - bw * 0.15 + i * bw * 0.1).toFloat(), (x + side * bw * 0.46).toFloat(), (y - bw * 0.3 + i * bw * 0.2).toFloat(), stroke)
            }
            }
            }
            41 -> { // VIPER DREAD: Twin coiled vipers facing opposite directions
                for (side in floatArrayOf(-1f, 1f)) {
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawArc((x+side*bw*0.15-bw*0.15).toFloat(), (y-bw*0.3).toFloat(), (x+side*bw*0.15+bw*0.15).toFloat(), (y+bw*0.3).toFloat(), (0).toFloat(), ((360).toFloat() - (0).toFloat()), false, stroke)
                path.reset()
                path.moveTo((x + side * bw * 0.15).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x + side * bw * 0.25).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x + side * bw * 0.05).toFloat(), (y - bw * 0.45).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            42 -> { // SHADOW GARGOYLE: Winged demon skull with horns and bat wings
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.2).toFloat())
                path.lineTo((x - bw * 0.18).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + bw * 0.18).toFloat(), (y - bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                // Horns
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawLine((x - bw * 0.15).toFloat(), (y - bw * 0.15).toFloat(), (x - bw * 0.35).toFloat(), (y - bw * 0.4).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawLine((x + bw * 0.15).toFloat(), (y - bw * 0.15).toFloat(), (x + bw * 0.4).toFloat(), (y - bw * 0.4).toFloat(), stroke)
                // Bat wings
                for (side in floatArrayOf(-1f, 1f)) {
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.05).toFloat())
                path.lineTo((x + side * bw * 0.48).toFloat(), (y - bw * 0.25).toFloat())
                path.lineTo((x + side * bw * 0.35).toFloat(), (y + bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
            }
            43 -> { // ARMORED ANCHORITE: Walking naval fortress with massive claw braces
                c.drawRect((x-bw*0.35).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.2).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.35).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.2).toFloat(), stroke)
                c.drawOval((x-bw*0.14).toFloat(), (y-bw*0.14).toFloat(), (x+bw*0.14).toFloat(), (y+bw*0.14).toFloat(), fill.apply { color = col })
                // Side claw braces
                for (side in floatArrayOf(-1f, 1f)) {
                path.reset()
                path.moveTo((x + side * bw * 0.35).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x + side * bw * 0.48).toFloat(), (y).toFloat())
                path.lineTo((x + side * bw * 0.35).toFloat(), (y + bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            44 -> { // OCTOPUS PRIMARCH: Domed mantle with 8 radial segmented tentacle arms
                c.drawOval((x-bw*0.25).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.25).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.25).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.25).toFloat(), stroke)
                c.drawOval((x-bw*0.1).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.1).toFloat(), (y+bw*0.1).toFloat(), fill.apply { color = col })
                for (i in 0 until 8) {
                val a = (i * PI / 4).toFloat()
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x + cos(a) * bw * 0.25).toFloat(), (y + sin(a) * bw * 0.25).toFloat(), (x + cos(a) * bw * 0.46).toFloat(), (y + sin(a) * bw * 0.46).toFloat(), stroke)
            }
            45 -> { // GIGANTIC SCORPION QUEEN: Broad body, twin segmented tail stingers
                c.drawOval((x-bw*0.22).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.35).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.22).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.35).toFloat(), stroke)
                // Twin curved stingers
                for (side in floatArrayOf(-1f, 1f)) {
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawArc((x+side*bw*0.15-bw*0.15).toFloat(), (y-bw*0.42).toFloat(), (x+side*bw*0.15+bw*0.15).toFloat(), (y+bw*0.1).toFloat(), (180).toFloat(), ((360).toFloat() - (180).toFloat()), false, stroke)
                path.reset()
                path.moveTo((x + side * bw * 0.3).toFloat(), (y - bw * 0.25).toFloat())
                path.lineTo((x + side * bw * 0.38).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x + side * bw * 0.2).toFloat(), (y - bw * 0.35).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            46 -> { // CHITIN DRONE: Heavy flying armored beetle with 4 articulated wing foils
                c.drawRect((x-bw*0.15).toFloat(), (y-bw*0.3).toFloat(), (x+bw*0.15).toFloat(), (y+bw*0.3).toFloat(), fill.apply { color = col })
                for (i in 0 until 2) {
                for (side in floatArrayOf(-1f, 1f)) {
                val segY = (y - bw*0.15 + i * bw*0.25).toFloat()
                path.reset()
                path.moveTo((x + side * bw * 0.15).toFloat(), (y).toFloat())
                path.lineTo((x + side * bw * 0.45).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + side * bw * 0.4).toFloat(), (y + bw * 0.1).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
            }
            }
            47 -> { // ANCIENT LEVIATHAN WYRM: 8-node spine wyrm with huge horned predator head
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x - bw * 0.25).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x + bw * 0.25).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                // Horns
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x - bw * 0.2).toFloat(), (y - bw * 0.2).toFloat(), (x - bw * 0.4).toFloat(), (y - bw * 0.35).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x + bw * 0.2).toFloat(), (y - bw * 0.2).toFloat(), (x + bw * 0.4).toFloat(), (y - bw * 0.35).toFloat(), stroke)
                for (i in 0 until 6) {
                val segY = (y - bw*0.1 + i * bw*0.09).toFloat()
                c.drawOval((x-bw*0.18+i*2).toFloat(), (y-6).toFloat(), (x+bw*0.18-i*2).toFloat(), (y+6).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.18+i*2).toFloat(), (y-6).toFloat(), (x+bw*0.18-i*2).toFloat(), (y+6).toFloat(), stroke)
            }
            48 -> { // VOID SPHINX: Winged guardian lion with armored crown and laser chest
                c.drawRect((x-bw*0.2).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.3).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.2).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.3).toFloat(), stroke)
                // Crown head
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x - bw * 0.18).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + bw * 0.18).toFloat(), (y - bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                c.drawOval((x-bw*0.1).toFloat(), (y).toFloat(), (x+bw*0.1).toFloat(), (y+bw*0.18).toFloat(), fill.apply { color = col })
                for (side in floatArrayOf(-1f, 1f)) {
                path.reset()
                path.moveTo((x + side * bw * 0.2).toFloat(), (y - bw * 0.1).toFloat())
                path.lineTo((x + side * bw * 0.45).toFloat(), (y - bw * 0.3).toFloat())
                path.lineTo((x + side * bw * 0.38).toFloat(), (y + bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            49 -> { // THE OMEGA HYPER-CORE (FINAL 50TH BOSS): The ultimate planetary singularity entity
                val r1 = (bw*0.45).toFloat()
                val r2 = (bw*0.32).toFloat()
                val r3 = (bw*0.16).toFloat()
                stroke.color = col; stroke.strokeWidth = 6f
                c.drawOval((x-r1).toFloat(), (y-r1).toFloat(), (x+r1).toFloat(), (y+r1).toFloat(), stroke)
                stroke.color = GRAY; stroke.strokeWidth = 4f
                c.drawOval((x-r2).toFloat(), (y-r2).toFloat(), (x+r2).toFloat(), (y+r2).toFloat(), stroke)
                c.drawOval((x-r3).toFloat(), (y-r3).toFloat(), (x+r3).toFloat(), (y+r3).toFloat(), fill.apply { color = col })
                c.drawOval((x-5).toFloat(), (y-5).toFloat(), (x+5).toFloat(), (y+5).toFloat(), fill.apply { color = inv })
                // 8 radiating mechanical tentacles
                for (i in 0 until 8) {
                val a = (i * PI / 4).toFloat()
                val tx = (x + cos(a)*r1).toFloat()
                val ty = (y + sin(a)*r1).toFloat()
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((tx).toFloat(), (ty).toFloat(), (tx + cos(a) * 14).toFloat(), (ty + sin(a) * 14).toFloat(), stroke)
                c.drawRect((tx+cos(a)*14-4).toFloat(), (ty+sin(a)*14-4).toFloat(), (tx+cos(a)*14+4).toFloat(), (ty+sin(a)*14+4).toFloat(), fill.apply { color = col })
            }
            }