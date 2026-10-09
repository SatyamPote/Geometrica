import PIL.Image as Image
import PIL.ImageDraw as ImageDraw
import math
import re

img = Image.new('RGB', (1600, 1600), (8, 14, 11))
draw = ImageDraw.Draw(img)
c = (195, 230, 205)
bg = (8, 14, 11)
inv = (120, 160, 130)

cols = 10
rows = 10
cell_w = 160
cell_h = 160

with open(r'c:\Users\satya\Documents\BambooKit\Geometrica\SpaceImpactAndroid\all_100_roster.kt', 'r') as f:
    text = f.read()

names = re.findall(r'EnemySpec\("([^"]+)"', text)

for i, name in enumerate(names):
    gx = i % cols
    gy = i // cols
    cx = gx * cell_w + cell_w // 2
    cy = gy * cell_h + cell_h // 2 - 12
    s = 28.0
    
    # draw cell box
    draw.rectangle([gx * cell_w + 3, gy * cell_h + 3, (gx+1) * cell_w - 3, (gy+1) * cell_h - 3], outline=(25, 45, 35), width=1)
    
    # Distinct shape based on archetype family & index
    family = i % 12
    if family == 0: # DRIFT (horizontal winged glider)
        draw.polygon([(cx, cy + s*1.1), (cx - s*1.1, cy - s*0.6), (cx, cy - s*0.2), (cx + s*1.1, cy - s*0.6)], outline=c, width=2)
        draw.ellipse([cx-4, cy-4, cx+4, cy+4], fill=c)
    elif family == 1: # TRACK (needle dart fighter)
        draw.polygon([(cx, cy + s*1.3), (cx - s*0.5, cy - s*0.9), (cx + s*0.5, cy - s*0.9)], outline=c, width=2)
        draw.line([(cx - s*0.8, cy - s*0.2), (cx + s*0.8, cy - s*0.2)], fill=c, width=2)
    elif family == 2: # DIVE (swept arrow raptor)
        draw.polygon([(cx, cy + s*1.3), (cx - s*0.9, cy - s*0.5), (cx - s*0.3, cy - s*0.1), (cx, cy - s*0.5), (cx + s*0.3, cy - s*0.1), (cx + s*0.9, cy - s*0.5)], outline=c, width=2)
    elif family == 3: # WEAVE (sine wave diamond)
        draw.polygon([(cx, cy + s*1.1), (cx + s*0.8, cy), (cx, cy - s*1.1), (cx - s*0.8, cy)], outline=c, width=2)
        draw.line([(cx, cy - s*1.1), (cx, cy + s*1.1)], fill=c, width=1)
    elif family == 4: # GUN (twin sponson heavy cruiser)
        draw.rectangle([cx - s*0.5, cy - s*0.7, cx + s*0.5, cy + s*0.7], outline=c, width=2)
        draw.rectangle([cx - s*0.9, cy - s*0.4, cx - s*0.6, cy + s*0.9], fill=c)
        draw.rectangle([cx + s*0.6, cy - s*0.4, cx + s*0.9, cy + s*0.9], fill=c)
    elif family == 5: # BURST (explosive cluster star)
        pts = []
        for st in range(8):
            a = st * math.pi / 4
            r = s * (1.1 if st % 2 == 0 else 0.5)
            pts.append((cx + math.cos(a)*r, cy + math.sin(a)*r))
        draw.polygon(pts, outline=c, width=2)
    elif family == 6: # SPIRAL (curved vortex orb)
        draw.ellipse([cx - s*0.8, cy - s*0.8, cx + s*0.8, cy + s*0.8], outline=c, width=2)
        draw.ellipse([cx - s*0.3, cy - s*0.3, cx + s*0.3, cy + s*0.3], fill=c)
        draw.arc([cx - s*1.1, cy - s*1.1, cx + s*1.1, cy + s*1.1], 0, 180, fill=c, width=2)
    elif family == 7: # SHIELD (fortified aegis bar)
        draw.polygon([(cx, cy + s*1.0), (cx - s*0.8, cy - s*0.6), (cx + s*0.8, cy - s*0.6)], outline=c, width=2)
        draw.line([(cx - s*1.1, cy + s*0.6), (cx + s*1.1, cy + s*0.6)], fill=c, width=3)
    elif family == 8: # KAMI (spiked triangle torpedo)
        draw.polygon([(cx, cy + s*1.4), (cx - s*0.4, cy - s*0.8), (cx + s*0.4, cy - s*0.8)], fill=c)
    elif family == 9: # SNIPER (long lance focal prism)
        draw.line([(cx, cy - s*1.2), (cx, cy + s*1.4)], fill=c, width=3)
        draw.rectangle([cx - s*0.5, cy - s*0.3, cx + s*0.5, cy + s*0.3], outline=c, width=2)
    elif family == 10: # SPLIT (dual conjoined pods)
        draw.ellipse([cx - s*0.8, cy - s*0.5, cx - s*0.1, cy + s*0.5], outline=c, width=2)
        draw.ellipse([cx + s*0.1, cy - s*0.5, cx + s*0.8, cy + s*0.5], outline=c, width=2)
        draw.line([(cx - s*0.1, cy), (cx + s*0.1, cy)], fill=c, width=2)
    else: # STRAFE (cruciform raider)
        draw.rectangle([cx - s*1.1, cy - s*0.25, cx + s*1.1, cy + s*0.25], fill=c)
        draw.rectangle([cx - s*0.25, cy - s*0.9, cx + s*0.25, cy + s*0.9], outline=c, width=2)

    # Label text
    draw.text((cx - len(name)*3.2, cy + s + 12), f'{i+1}. {name}', fill=(160, 200, 170))

img.save(r'c:\Users\satya\Documents\BambooKit\Geometrica\all_aliens_preview.png')
print('Success generating all_aliens_preview.png with 100 aliens!')
