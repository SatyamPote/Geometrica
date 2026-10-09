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

with open(r'c:\Users\satya\Documents\BambooKit\Geometrica\SpaceImpactAndroid\app\src\main\java\com\nebulastrike\game\PlayerShipCatalog.kt', 'r') as f:
    text = f.read()

ships = re.findall(r'PlayerShipInfo\((\d+),\s*"([^"]+)",\s*"([^"]+)",\s*"([^"]+)"', text)

for i, (num, name, ship_id, diff) in enumerate(ships):
    gx = i % cols
    gy = i // cols
    cx = gx * cell_w + cell_w // 2
    cy = gy * cell_h + cell_h // 2 - 12
    s = 28.0
    
    # draw cell box
    draw.rectangle([gx * cell_w + 3, gy * cell_h + 3, (gx+1) * cell_w - 3, (gy+1) * cell_h - 3], outline=(25, 45, 35), width=1)
    
    # Distinct shape based on archetype
    archetype = i % 20
    tier = i // 20
    
    if archetype == 0: # Interceptor
        draw.polygon([(cx, cy - s*1.3), (cx + s*(0.35 + tier*0.06), cy + s*0.6), (cx, cy + s*0.4), (cx - s*(0.35 + tier*0.06), cy + s*0.6)], outline=c, width=2)
        draw.ellipse([cx-4, cy-4, cx+4, cy+4], fill=c)
    elif archetype == 1: # Striker
        draw.polygon([(cx, cy - s*1.1), (cx + s*0.75, cy + s*0.7), (cx + s*0.25, cy + s*0.5), (cx, cy + s*0.7), (cx - s*0.25, cy + s*0.5), (cx - s*0.75, cy + s*0.7)], outline=c, width=2)
    elif archetype == 2: # Defender
        pts = []
        for st in range(6):
            a = st * math.pi / 3 - math.pi / 2
            pts.append((cx + math.cos(a)*s*0.85, cy + math.sin(a)*s*0.85))
        draw.polygon(pts, outline=c, width=2)
        draw.ellipse([cx-5, cy-5, cx+5, cy+5], fill=c)
    elif archetype == 3: # Raider
        draw.polygon([(cx, cy - s*1.2), (cx + s*0.85, cy + s*0.4), (cx + s*0.3, cy + s*0.8), (cx - s*0.45, cy + s*0.7), (cx - s*0.85, cy + s*0.2)], outline=c, width=2)
    elif archetype == 4: # Bomber
        draw.rectangle([cx - s*0.75, cy - s*0.8, cx - s*0.25, cy + s*0.8], outline=c, width=2)
        draw.rectangle([cx + s*0.25, cy - s*0.8, cx + s*0.75, cy + s*0.8], outline=c, width=2)
        draw.rectangle([cx - s*0.35, cy - s*0.2, cx + s*0.35, cy + s*0.3], fill=c)
    elif archetype == 5: # Scout
        draw.polygon([(cx, cy - s*1.4), (cx + s*0.5, cy), (cx, cy + s*0.9), (cx - s*0.5, cy)], outline=c, width=2)
        draw.line([(cx, cy - s*1.2), (cx, cy + s*0.7)], fill=c, width=1)
    elif archetype == 6: # Vanguard
        draw.rectangle([cx - s*0.18, cy - s*1.2, cx + s*0.18, cy + s*0.9], fill=c)
        draw.rectangle([cx - s*0.85, cy - s*0.2, cx + s*0.85, cy + s*0.3], outline=c, width=2)
    elif archetype == 7: # Extinguisher
        draw.polygon([(cx - s*0.5, cy - s*1.1), (cx - s*0.2, cy - s*0.2), (cx + s*0.2, cy - s*0.2), (cx + s*0.5, cy - s*1.1), (cx + s*0.65, cy + s*0.7), (cx - s*0.65, cy + s*0.7)], outline=c, width=2)
    elif archetype == 8: # Pursuer
        draw.polygon([(cx, cy - s*1.25), (cx + s*0.3, cy + s*0.6), (cx + s*0.85, cy + s*0.9), (cx, cy + s*0.4), (cx - s*0.85, cy + s*0.9), (cx - s*0.3, cy + s*0.6)], outline=c, width=2)
    elif archetype == 9: # Annihilator
        draw.rectangle([cx - s*0.85, cy - s*1.1, cx + s*0.85, cy - s*0.5], fill=c)
        draw.rectangle([cx - s*0.35, cy - s*0.5, cx + s*0.35, cy + s*0.8], outline=c, width=2)
    elif archetype == 10: # Guardian
        draw.ellipse([cx - s*0.7, cy - s*0.7, cx + s*0.7, cy + s*0.7], outline=c, width=2)
        draw.rectangle([cx - s*0.15, cy - s*1.2, cx + s*0.15, cy - s*0.7], fill=c)
    elif archetype == 11: # Interceptor-Elite
        draw.polygon([(cx, cy - s*1.45), (cx + s*0.65, cy + s*0.7), (cx, cy + s*0.4), (cx - s*0.65, cy + s*0.7)], outline=c, width=2)
        draw.ellipse([cx - s*0.35 - 3, cy + s*0.4 - 3, cx - s*0.35 + 3, cy + s*0.4 + 3], fill=c)
        draw.ellipse([cx + s*0.35 - 3, cy + s*0.4 - 3, cx + s*0.35 + 3, cy + s*0.4 + 3], fill=c)
    elif archetype == 12: # Striker-Elite
        draw.polygon([(cx, cy - s*0.8), (cx + s*0.95, cy - s*0.3), (cx + s*0.4, cy + s*0.8), (cx, cy + s*0.35), (cx - s*0.4, cy + s*0.8), (cx - s*0.95, cy - s*0.3)], outline=c, width=2)
    elif archetype == 13: # Defender-Elite
        pts = []
        for st in range(8):
            a = st * math.pi / 4 - math.pi / 8
            pts.append((cx + math.cos(a)*s*0.85, cy + math.sin(a)*s*0.85))
        draw.polygon(pts, outline=c, width=2)
    elif archetype == 14: # Raider-Elite
        draw.polygon([(cx, cy - s*1.3), (cx + s*0.8, cy + s*0.8), (cx, cy + s*0.4), (cx - s*0.8, cy + s*0.8)], outline=c, width=2)
        draw.polygon([(cx, cy - s*0.7), (cx + s*0.4, cy + s*0.3), (cx, cy + s*0.1), (cx - s*0.4, cy + s*0.3)], fill=c)
    elif archetype == 15: # Bomber-Elite
        draw.rectangle([cx - s*0.85, cy - s*0.7, cx - s*0.45, cy + s*0.8], outline=c, width=2)
        draw.rectangle([cx + s*0.45, cy - s*0.7, cx + s*0.85, cy + s*0.8], outline=c, width=2)
        draw.rectangle([cx - s*0.25, cy - s*1.1, cx + s*0.25, cy + s*0.85], fill=c)
    elif archetype == 16: # Scout-Elite
        draw.ellipse([cx - s*0.35, cy - s*0.85, cx + s*0.35, cy - s*0.15], outline=c, width=2)
        draw.ellipse([cx - s*0.8, cy, cx - s*0.1, cy + s*0.7], outline=c, width=2)
        draw.ellipse([cx + s*0.1, cy, cx + s*0.8, cy + s*0.7], outline=c, width=2)
    elif archetype == 17: # Vanguard-Elite
        for k in range(3):
            kcy = cy - s*0.8 + k*s*0.65
            draw.polygon([(cx, kcy - s*0.4), (cx + s*0.65, kcy + s*0.3), (cx, kcy + s*0.1), (cx - s*0.65, kcy + s*0.3)], outline=c, width=2)
    elif archetype == 18: # Extinguisher-Elite
        draw.rectangle([cx - s*0.18, cy - s*1.2, cx + s*0.18, cy + s*0.9], fill=c)
        draw.rectangle([cx - s*0.7, cy - s*0.4, cx + s*0.7, cy - s*0.05], fill=c)
        draw.ellipse([cx - s*0.22, cy - s*1.02, cx + s*0.22, cy - s*0.58], outline=c, width=2)
    else: # Pursuer-Elite
        pts = []
        for st in range(5):
            a = st * 4 * math.pi / 5 - math.pi / 2
            pts.append((cx + math.cos(a)*s*0.95, cy + math.sin(a)*s*0.95))
        draw.polygon(pts, outline=c, width=2)
        draw.ellipse([cx-4, cy-4, cx+4, cy+4], fill=c)

    # Text label
    draw.text((cx - len(name)*3.2, cy + s + 12), f'{num}. {name.upper()}', fill=(160, 200, 170))

img.save(r'c:\Users\satya\Documents\BambooKit\Geometrica\all_100_player_ships_preview.png')
print('Success generating all_100_player_ships_preview.png!')
