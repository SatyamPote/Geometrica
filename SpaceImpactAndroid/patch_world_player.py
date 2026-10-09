with open(r'c:\Users\satya\Documents\BambooKit\Geometrica\SpaceImpactAndroid\app\src\main\java\com\nebulastrike\game\World.kt', 'rb') as f:
    text = f.read().decode('utf-8')

# Apply selected ship stats on reset
old_p_reset = """        val p = player
        p.x = w / 2f; p.y = h * 0.78f
        p.hits = 15; p.invuln = 1f; p.fireCd = 0f
        p.rapidT = 0f; p.doubleT = 0f; p.spreadT = 0f; p.pierceT = 0f
        p.dead = false; p.deathT = 0f"""

new_p_reset = """        val p = player
        val ship = ALL_100_PLAYER_SHIPS[save.shipIndex()]
        p.x = w / 2f; p.y = h * 0.78f
        p.hits = (ship.health / 5).coerceIn(6, 40)
        p.invuln = 1f; p.fireCd = 0f
        p.rapidT = 0f; p.doubleT = 0f; p.spreadT = 0f; p.pierceT = 0f
        p.dead = false; p.deathT = 0f"""

if old_p_reset in text:
    text = text.replace(old_p_reset, new_p_reset)
    print("Updated World.reset with selected ship health!")
else:
    old_p_crlf = old_p_reset.replace('\n', '\r\n')
    if old_p_crlf in text:
        text = text.replace(old_p_crlf, new_p_reset.replace('\n', '\r\n'))
        print("Updated World.reset with selected ship health (CRLF)!")

# Update firePlayer interval and speed based on ship stats and skills
old_fire = """        val interval = if (p.rapidT > 0) 0.075f else 0.14f
        if (p.fireCd > 0) return
        p.fireCd = interval
        sound.shoot()
        val sx = p.x
        val sy = p.y - 34f
        fun shot(x: Float, y: Float, vx: Float, vy: Float, dmg: Float, big: Boolean) {
            if (shots.size > 300) shots.removeAt(0)
            val b = Bullet()
            b.x = x; b.y = y; b.vx = vx; b.vy = vy
            b.dmg = dmg; b.big = big; b.pierce = p.pierceT > 0
            shots.add(b)
        }
        val dmg = 36f"""

new_fire = """        val ship = ALL_100_PLAYER_SHIPS[save.shipIndex()]
        val baseInterval = (0.16f - ship.speed * 0.012f).coerceAtLeast(0.06f)
        val interval = if (p.rapidT > 0 || ship.skill.contains("rapid")) baseInterval * 0.75f else baseInterval
        if (p.fireCd > 0) return
        p.fireCd = interval
        sound.shoot()
        val sx = p.x
        val sy = p.y - 34f
        fun shot(x: Float, y: Float, vx: Float, vy: Float, dmg: Float, big: Boolean) {
            if (shots.size > 300) shots.removeAt(0)
            val b = Bullet()
            b.x = x; b.y = y; b.vx = vx; b.vy = vy
            b.dmg = dmg; b.big = big; b.pierce = p.pierceT > 0 || ship.skill.contains("pierce")
            shots.add(b)
        }
        val dmg = 24f + ship.power * 6f"""

if old_fire in text:
    text = text.replace(old_fire, new_fire)
    print("Updated firePlayer with ship power & speed!")
else:
    old_fire_crlf = old_fire.replace('\n', '\r\n')
    if old_fire_crlf in text:
        text = text.replace(old_fire_crlf, new_fire.replace('\n', '\r\n'))
        print("Updated firePlayer with ship power & speed (CRLF)!")

with open(r'c:\Users\satya\Documents\BambooKit\Geometrica\SpaceImpactAndroid\app\src\main\java\com\nebulastrike\game\World.kt', 'wb') as f:
    f.write(text.encode('utf-8'))
