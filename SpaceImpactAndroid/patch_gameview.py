with open(r'c:\Users\satya\Documents\BambooKit\Geometrica\SpaceImpactAndroid\app\src\main\java\com\nebulastrike\game\GameView.kt', 'rb') as f:
    text = f.read().decode('utf-8')

# 1. Update drawPlayer call to pass selected ship
old_draw = "Art.drawPlayer(c, p.x, p.y, 30f, t, p.invuln > 0)"
new_draw = "Art.drawPlayer(c, p.x, p.y, 30f, t, p.invuln > 0, save.shipIndex())"
if old_draw in text:
    text = text.replace(old_draw, new_draw)
    print("Replaced drawPlayer call in GameView.kt")

# 2. Add ship selector UI in drawTitle
old_title_end = """        c.drawText("SND " + if (save.snd()) "ON" else "OFF", w * 0.25f, h - 40f, txt)
        c.drawText("FX " + if (save.fx()) "ON" else "OFF", w * 0.75f, h - 40f, txt)
    }"""

new_title_end = """        c.drawText("SND " + if (save.snd()) "ON" else "OFF", w * 0.25f, h - 40f, txt)
        c.drawText("FX " + if (save.fx()) "ON" else "OFF", w * 0.75f, h - 40f, txt)

        // Hangar Ship Selector
        val curShip = ALL_100_PLAYER_SHIPS[save.shipIndex()]
        txt.textSize = 18f * resources.displayMetrics.scaledDensity / 2.2f
        txt.color = WHITE
        centerText(c, "<  SHIP #${curShip.num}: ${curShip.name.uppercase()}  >", w / 2f, h * 0.50f, 20f)
        txt.textSize = 14f * resources.displayMetrics.scaledDensity / 2.2f
        txt.color = GRAY
        centerText(c, "SKILL: [${curShip.skill.uppercase()}]  HP: ${curShip.health}  SPD: ${curShip.speed}", w / 2f, h * 0.54f, 15f)
        Art.drawPlayer(c, w / 2f, h * 0.44f, 38f, t, false, save.shipIndex())
    }"""

if old_title_end in text:
    text = text.replace(old_title_end, new_title_end)
    print("Added hangar ship selector UI to drawTitle")
else:
    # check CRLF
    old_crlf = old_title_end.replace('\n', '\r\n')
    if old_crlf in text:
        text = text.replace(old_crlf, new_title_end.replace('\n', '\r\n'))
        print("Added hangar ship selector UI with CRLF")

# 3. Add touch handling to switch ship on title screen
old_touch = """                    if (state == State.TITLE && e.actionMasked == MotionEvent.ACTION_DOWN && y > height - dp(90f)) {
                        if (x < width / 2f) {
                            save.setSnd(!save.snd())
                            sound.on = save.snd()
                            sound.music.enabled = save.snd()
                        } else {
                            save.setFx(!save.fx())
                        }
                        sound.click()
                        return true
                    }"""

new_touch = """                    if (state == State.TITLE && e.actionMasked == MotionEvent.ACTION_DOWN && y > height - dp(90f)) {
                        if (x < width / 2f) {
                            save.setSnd(!save.snd())
                            sound.on = save.snd()
                            sound.music.enabled = save.snd()
                        } else {
                            save.setFx(!save.fx())
                        }
                        sound.click()
                        return true
                    }
                    if (state == State.TITLE && e.actionMasked == MotionEvent.ACTION_DOWN && y > height * 0.40f && y < height * 0.58f) {
                        if (x < width * 0.4f) {
                            save.setShipIndex((save.shipIndex() - 1 + 100) % 100)
                        } else {
                            save.setShipIndex((save.shipIndex() + 1) % 100)
                        }
                        sound.click()
                        return true
                    }"""

if old_touch in text:
    text = text.replace(old_touch, new_touch)
    print("Added touch ship cycling in onTouchEvent")
else:
    old_touch_crlf = old_touch.replace('\n', '\r\n')
    if old_touch_crlf in text:
        text = text.replace(old_touch_crlf, new_touch.replace('\n', '\r\n'))
        print("Added touch ship cycling with CRLF")

with open(r'c:\Users\satya\Documents\BambooKit\Geometrica\SpaceImpactAndroid\app\src\main\java\com\nebulastrike\game\GameView.kt', 'wb') as f:
    f.write(text.encode('utf-8'))
