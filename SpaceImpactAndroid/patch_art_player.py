with open(r'c:\Users\satya\Documents\BambooKit\Geometrica\SpaceImpactAndroid\app\src\main\java\com\nebulastrike\game\Art.kt', 'rb') as f:
    content = f.read().decode('utf-8')

old_code = """    // ---------------- player (faces up) ----------------
    fun drawPlayer(c: Canvas, x: Float, y: Float, s: Float, t: Long, blink: Boolean) {"""

new_code = """    // ---------------- player (faces up) ----------------
    fun drawPlayer(c: Canvas, x: Float, y: Float, s: Float, t: Long, blink: Boolean, shipIdx: Int = 0) {
        PlayerShipRenderer.drawShip(c, shipIdx, x, y, s, t, blink)
        return
    }
    fun drawPlayerLegacy(c: Canvas, x: Float, y: Float, s: Float, t: Long, blink: Boolean) {"""

# Replace with unix or crlf awareness
if old_code in content:
    content = content.replace(old_code, new_code, 1)
    print("Found and replaced via direct substring!")
else:
    # try replacing line endings
    content_crlf = content.replace('\r\n', '\n')
    if old_code.replace('\r\n', '\n') in content_crlf:
        content_crlf = content_crlf.replace(old_code.replace('\r\n', '\n'), new_code.replace('\r\n', '\n'), 1)
        content = content_crlf
        print("Found and replaced via normalized newlines!")
    else:
        print("Pattern still not found!")

with open(r'c:\Users\satya\Documents\BambooKit\Geometrica\SpaceImpactAndroid\app\src\main\java\com\nebulastrike\game\Art.kt', 'wb') as f:
    f.write(content.encode('utf-8'))
