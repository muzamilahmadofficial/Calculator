import os
import math
from PIL import Image, ImageDraw, ImageFont, ImageFilter

ASSETS_DIR = "playstore_assets"
SCREENSHOTS_DIR = os.path.join(ASSETS_DIR, "screenshots")
os.makedirs(SCREENSHOTS_DIR, exist_ok=True)

FONT_PATH = "app/src/main/res/font/plus_jakarta_sans.ttf"

def get_font(size, bold=False):
    try:
        return ImageFont.truetype(FONT_PATH, size)
    except Exception:
        return ImageFont.load_default()

def draw_rounded_rect(draw, box, radius, fill=None, outline=None, width=1):
    draw.rounded_rectangle(box, radius=radius, fill=fill, outline=outline, width=width)

def draw_linear_gradient(size, start_color, end_color, vertical=True):
    base = Image.new("RGBA", size, start_color)
    top = Image.new("RGBA", size, end_color)
    mask = Image.new("L", size)
    mask_data = []
    w, h = size
    for y in range(h):
        for x in range(w):
            val = int(255 * (y / h if vertical else x / w))
            mask_data.append(val)
    mask.putdata(mask_data)
    base.paste(top, (0, 0), mask)
    return base

# ==========================================
# 1. 512x512 HIGH RES APP ICON
# ==========================================
def generate_icon():
    print("Generating 512x512 App Icon...")
    size = (512, 512)
    img = Image.new("RGBA", size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Squircle background
    pad = 20
    box = [pad, pad, size[0] - pad, size[1] - pad]
    radius = 105

    # Shadow
    shadow = Image.new("RGBA", size, (0, 0, 0, 0))
    sdraw = ImageDraw.Draw(shadow)
    sdraw.rounded_rectangle([pad, pad + 10, size[0] - pad, size[1] - pad + 10], radius=radius, fill=(0, 0, 0, 140))
    shadow = shadow.filter(ImageFilter.GaussianBlur(14))
    img.paste(shadow, (0, 0), shadow)

    # Base gradient fill
    bg_gradient = draw_linear_gradient(size, (28, 32, 42, 255), (14, 17, 23, 255), vertical=True)
    mask = Image.new("L", size, 0)
    mdraw = ImageDraw.Draw(mask)
    mdraw.rounded_rectangle(box, radius=radius, fill=255)
    img.paste(bg_gradient, (0, 0), mask)

    # Border stroke
    draw.rounded_rectangle(box, radius=radius, outline=(255, 149, 0, 80), width=3)

    # Central Calculator Visual
    calc_box = [95, 80, 417, 432]
    draw.rounded_rectangle(calc_box, radius=48, fill=(24, 28, 38, 255), outline=(50, 58, 75, 255), width=2)

    # Calculator Screen display
    screen_box = [120, 105, 392, 185]
    draw.rounded_rectangle(screen_box, radius=20, fill=(10, 12, 17, 255), outline=(35, 42, 55, 255), width=2)

    # Screen text: "MA" logo on left, "2026" or result on right
    font_screen_logo = get_font(28)
    font_screen_res = get_font(38)
    draw.text((140, 128), "MA", font=font_screen_logo, fill=(255, 158, 30, 255))
    draw.text((275, 120), "3.1415", font=font_screen_res, fill=(255, 255, 255, 240))

    # Keypad Grid (3x3 round tactile buttons)
    key_r = 30
    keys = [
        ("C", (255, 107, 107)), ("±", (180, 190, 205)), ("%", (180, 190, 205)), ("÷", (255, 158, 30)),
        ("7", (240, 245, 255)), ("8", (240, 245, 255)), ("9", (240, 245, 255)), ("×", (255, 158, 30)),
        ("4", (240, 245, 255)), ("5", (240, 245, 255)), ("6", (240, 245, 255)), ("−", (255, 158, 30)),
        ("1", (240, 245, 255)), ("2", (240, 245, 255)), ("3", (240, 245, 255)), ("+", (255, 158, 30)),
    ]
    # We draw 4x3 compact layout
    xs = [148, 218, 288, 358]
    ys = [225, 282, 339, 396]
    f_key = get_font(24)

    idx = 0
    for r in range(4):
        for c in range(4):
            x, y = xs[c], ys[r]
            symbol, color = keys[idx]
            is_accent = (c == 3)
            btn_bg = (255, 149, 0, 255) if is_accent else (36, 42, 56, 255)
            text_color = (255, 255, 255, 255) if is_accent else color
            draw.ellipse([x - 24, y - 24, x + 24, y + 24], fill=btn_bg, outline=(60, 70, 90, 180) if not is_accent else None, width=1)
            # Center symbol
            bbox = f_key.getbbox(symbol)
            tw = bbox[2] - bbox[0]
            th = bbox[3] - bbox[1]
            draw.text((x - tw // 2, y - th // 2 - 2), symbol, font=f_key, fill=text_color)
            idx += 1

    out_path = os.path.join(ASSETS_DIR, "icon_512x512.png")
    img.save(out_path, "PNG")
    print(f"Icon saved: {out_path}")

# ==========================================
# 2. 1024x500 FEATURE GRAPHIC
# ==========================================
def generate_feature_graphic():
    print("Generating 1024x500 Feature Graphic...")
    size = (1024, 500)
    img = Image.new("RGBA", size, (13, 16, 22, 255))
    draw = ImageDraw.Draw(img)

    # Ambient orange glow in center-right
    glow = Image.new("RGBA", size, (0, 0, 0, 0))
    gdraw = ImageDraw.Draw(glow)
    gdraw.ellipse([500, -100, 1100, 500], fill=(255, 120, 0, 45))
    gdraw.ellipse([700, 100, 1050, 450], fill=(255, 80, 0, 40))
    glow = glow.filter(ImageFilter.GaussianBlur(60))
    img.paste(glow, (0, 0), glow)

    # Left content: Titles & Highlights
    # Category tag pill
    pill_box = [60, 65, 290, 105]
    draw.rounded_rectangle(pill_box, radius=20, fill=(255, 149, 0, 30), outline=(255, 149, 0, 180), width=1)
    f_pill = get_font(16)
    draw.text((80, 75), "★ ALL-IN-ONE PRO TOOL", font=f_pill, fill=(255, 175, 55, 255))

    # Main Heading
    f_title = get_font(52)
    draw.text((60, 120), "MA Calculator", font=f_title, fill=(255, 255, 255, 255))

    # Subtitle
    f_sub = get_font(23)
    draw.text((62, 190), "Fast, Scientific & Unit Converter", font=f_sub, fill=(255, 158, 30, 255))

    # Feature checklist pills
    features = [
        "✓  100% Private & Fully Offline",
        "✓  Scientific Math (Trig, Log, Powers)",
        "✓  Smart Unit Converter (5 Categories)",
        "✓  Instant Calculation History (SQLite)"
    ]
    f_feat = get_font(18)
    for i, feat in enumerate(features):
        y = 245 + i * 40
        draw.text((64, y), feat, font=f_feat, fill=(200, 210, 225, 240))

    # Right side: Mockup Card of Calculator UI
    mockup_box = [620, 45, 960, 455]
    # Card shadow
    m_shadow = Image.new("RGBA", size, (0, 0, 0, 0))
    ms_draw = ImageDraw.Draw(m_shadow)
    ms_draw.rounded_rectangle(mockup_box, radius=32, fill=(0, 0, 0, 180))
    m_shadow = m_shadow.filter(ImageFilter.GaussianBlur(25))
    img.paste(m_shadow, (0, 0), m_shadow)

    # Card body
    draw.rounded_rectangle(mockup_box, radius=32, fill=(21, 25, 34, 255), outline=(50, 60, 78, 255), width=2)

    # Card Screen
    cscreen = [645, 75, 935, 175]
    draw.rounded_rectangle(cscreen, radius=18, fill=(12, 14, 20, 255), outline=(38, 46, 60, 255), width=1)
    f_cs_sub = get_font(18)
    f_cs_main = get_font(38)
    draw.text((665, 90), "sin(45°) + 125 × 8 =", font=f_cs_sub, fill=(160, 175, 195, 255))
    draw.text((665, 120), "1,000.7071", font=f_cs_main, fill=(255, 255, 255, 255))

    # Compact keypad inside mockup
    f_mbtn = get_font(20)
    cols = [680, 750, 820, 890]
    rows = [215, 275, 335, 395]
    grid = [
        ("C", (255, 90, 90)), ("()", (180, 195, 215)), ("%", (180, 195, 215)), ("÷", (255, 150, 0)),
        ("7", (255, 255, 255)), ("8", (255, 255, 255)), ("9", (255, 255, 255)), ("×", (255, 150, 0)),
        ("4", (255, 255, 255)), ("5", (255, 255, 255)), ("6", (255, 255, 255)), ("−", (255, 150, 0)),
        ("1", (255, 255, 255)), ("2", (255, 255, 255)), ("3", (255, 255, 255)), ("=", (255, 150, 0))
    ]
    g_idx = 0
    for r in range(4):
        for c in range(4):
            x, y = cols[c], rows[r]
            lbl, col = grid[g_idx]
            is_accent = (c == 3)
            bg = (255, 149, 0, 255) if is_accent else (32, 38, 52, 255)
            draw.ellipse([x - 22, y - 22, x + 22, y + 22], fill=bg)
            b = f_mbtn.getbbox(lbl)
            w = b[2] - b[0]
            h = b[3] - b[1]
            draw.text((x - w // 2, y - h // 2 - 2), lbl, font=f_mbtn, fill=(255, 255, 255) if is_accent else col)
            g_idx += 1

    # Convert to RGB (Google Play strictly requires 24-bit PNG or JPEG without alpha)
    out_img = Image.new("RGB", size, (13, 16, 22))
    out_img.paste(img, (0, 0), img)
    out_path = os.path.join(ASSETS_DIR, "feature_graphic_1024x500.png")
    out_img.save(out_path, "PNG")
    print(f"Feature graphic saved: {out_path}")

# ==========================================
# 3. 5 HIGH RESOLUTION PHONE SCREENSHOTS (1080x2400)
# ==========================================
def draw_phone_frame(canvas, screen_img, pos=(120, 480), size=(840, 1780)):
    # Draw sleek modern bezel phone mockup around screen_img
    x, y = pos
    w, h = size
    draw = ImageDraw.Draw(canvas)

    # Outer phone shadow
    sh = Image.new("RGBA", canvas.size, (0, 0, 0, 0))
    sh_draw = ImageDraw.Draw(sh)
    sh_draw.rounded_rectangle([x - 24, y - 24, x + w + 24, y + h + 30], radius=70, fill=(0, 0, 0, 160))
    sh = sh.filter(ImageFilter.GaussianBlur(35))
    canvas.paste(sh, (0, 0), sh)

    # Phone Outer Bezel
    draw.rounded_rectangle([x - 14, y - 14, x + w + 14, y + h + 14], radius=62, fill=(24, 28, 38, 255), outline=(65, 75, 95, 255), width=3)

    # Screen inside bezel
    resized_screen = screen_img.resize((w, h), Image.Resampling.LANCZOS)
    mask = Image.new("L", (w, h), 0)
    mdraw = ImageDraw.Draw(mask)
    mdraw.rounded_rectangle([0, 0, w, h], radius=50, fill=255)
    canvas.paste(resized_screen, (x, y), mask)

    # Dynamic Island / Camera punch hole
    cx = x + w // 2
    draw.rounded_rectangle([cx - 45, y + 16, cx + 45, y + 42], radius=13, fill=(10, 12, 16, 255))

def create_base_screenshot_canvas(badge_text, title_text, desc_text):
    size = (1080, 2400)
    img = Image.new("RGBA", size, (14, 17, 24, 255))
    draw = ImageDraw.Draw(img)

    # Ambient background glow
    glow = Image.new("RGBA", size, (0, 0, 0, 0))
    gdraw = ImageDraw.Draw(glow)
    gdraw.ellipse([140, -100, 940, 600], fill=(255, 130, 0, 35))
    glow = glow.filter(ImageFilter.GaussianBlur(80))
    img.paste(glow, (0, 0), glow)

    # Top Badge
    pill_w = 340
    pill_h = 58
    pill_x = (size[0] - pill_w) // 2
    pill_y = 110
    draw.rounded_rectangle([pill_x, pill_y, pill_x + pill_w, pill_y + pill_h], radius=29, fill=(255, 149, 0, 25), outline=(255, 149, 0, 190), width=2)
    f_badge = get_font(22)
    b_box = f_badge.getbbox(badge_text)
    bw = b_box[2] - b_box[0]
    bh = b_box[3] - b_box[1]
    draw.text(((size[0] - bw) // 2, pill_y + (pill_h - bh) // 2 - 2), badge_text, font=f_badge, fill=(255, 170, 50, 255))

    # Title
    f_title = get_font(56)
    t_box = f_title.getbbox(title_text)
    tw = t_box[2] - t_box[0]
    draw.text(((size[0] - tw) // 2, 205), title_text, font=f_title, fill=(255, 255, 255, 255))

    # Description
    f_desc = get_font(28)
    d_box = f_desc.getbbox(desc_text)
    dw = d_box[2] - d_box[0]
    draw.text(((size[0] - dw) // 2, 290), desc_text, font=f_desc, fill=(180, 195, 215, 240))

    return img

def render_standard_screen():
    # 840 x 1780 simulated app screen
    size = (840, 1780)
    screen = Image.new("RGBA", size, (16, 20, 27, 255))
    draw = ImageDraw.Draw(screen)

    # Top App Bar
    f_brand = get_font(34)
    draw.text((50, 75), "MA Calculator", font=f_brand, fill=(255, 255, 255, 255))
    # Mode switch icons (Calculator, Converter, History)
    draw.rounded_rectangle([520, 68, 790, 120], radius=26, fill=(28, 35, 48, 255))
    f_icon = get_font(20)
    draw.text((545, 82), "Standard", font=f_icon, fill=(255, 150, 0, 255))
    draw.text((665, 82), "Converter", font=f_icon, fill=(160, 175, 195, 255))

    # Display Container
    disp_box = [40, 170, 800, 480]
    draw.rounded_rectangle(disp_box, radius=32, fill=(11, 14, 20, 255), outline=(35, 44, 58, 255), width=2)
    f_expr = get_font(36)
    f_res = get_font(84)
    draw.text((80, 230), "4,850 × 12.5% + 1,200", font=f_expr, fill=(150, 165, 185, 255))
    draw.text((80, 320), "1,806.25", font=f_res, fill=(255, 255, 255, 255))

    # Keypad
    keys = [
        ["C", "()", "%", "÷"],
        ["7", "8", "9", "×"],
        ["4", "5", "6", "−"],
        ["1", "2", "3", "+"],
        ["±", "0", ".", "="]
    ]
    f_btn = get_font(42)
    start_y = 540
    row_h = 220
    col_w = 180
    start_x = 65

    for r, row in enumerate(keys):
        for c, key in enumerate(row):
            kx = start_x + c * col_w
            ky = start_y + r * row_h
            is_accent = (key == "=")
            is_op = key in ["÷", "×", "−", "+"]
            is_top = key in ["C", "()", "%"]

            bg = (255, 149, 0, 255) if is_accent else ((40, 48, 64, 255) if is_op else ((32, 38, 50, 255) if is_top else (24, 28, 38, 255)))
            text_color = (255, 255, 255) if (is_accent or not is_top) else (255, 110, 110) if key == "C" else (180, 195, 215)
            if is_op:
                text_color = (255, 158, 30)

            draw.rounded_rectangle([kx, ky, kx + 155, ky + 175], radius=38, fill=bg, outline=(48, 58, 78, 255) if not is_accent else None, width=1)
            b = f_btn.getbbox(key)
            kw = b[2] - b[0]
            kh = b[3] - b[1]
            draw.text((kx + (155 - kw) // 2, ky + (175 - kh) // 2 - 4), key, font=f_btn, fill=text_color)

    return screen

def render_scientific_screen():
    size = (840, 1780)
    screen = Image.new("RGBA", size, (16, 20, 27, 255))
    draw = ImageDraw.Draw(screen)

    # Top App Bar
    f_brand = get_font(34)
    draw.text((50, 75), "MA Scientific Math", font=f_brand, fill=(255, 255, 255, 255))

    # Display Container
    disp_box = [40, 170, 800, 460]
    draw.rounded_rectangle(disp_box, radius=32, fill=(11, 14, 20, 255), outline=(35, 44, 58, 255), width=2)
    f_expr = get_font(36)
    f_res = get_font(80)
    draw.text((80, 220), "sin(π / 4) + ln(2.718)", font=f_expr, fill=(150, 165, 185, 255))
    draw.text((80, 310), "1.707106", font=f_res, fill=(255, 255, 255, 255))

    # Scientific Row Pills (RAD, DEG, sin, cos, tan, ln, log, √, ^, π, e)
    sci_keys = [
        ["RAD", "DEG", "INV", "π", "e"],
        ["sin", "cos", "tan", "ln", "log"],
        ["√", "^", "(", ")", "!"]
    ]
    f_sci = get_font(26)
    start_y = 510
    row_h = 100
    col_w = 145
    start_x = 55

    for r, row in enumerate(sci_keys):
        for c, key in enumerate(row):
            kx = start_x + c * col_w
            ky = start_y + r * row_h
            active = key == "RAD"
            bg = (255, 149, 0, 230) if active else (30, 36, 48, 255)
            tcol = (255, 255, 255) if active else (190, 205, 225)
            draw.rounded_rectangle([kx, ky, kx + 130, ky + 78], radius=24, fill=bg, outline=(48, 58, 78, 255) if not active else None, width=1)
            b = f_sci.getbbox(key)
            draw.text((kx + (130 - (b[2] - b[0])) // 2, ky + (78 - (b[3] - b[1])) // 2 - 2), key, font=f_sci, fill=tcol)

    # Standard Keypad Below
    keys = [
        ["7", "8", "9", "÷"],
        ["4", "5", "6", "×"],
        ["1", "2", "3", "−"],
        ["0", ".", "=", "+"]
    ]
    f_btn = get_font(38)
    num_start_y = 860
    nrow_h = 205
    ncol_w = 180
    nstart_x = 65

    for r, row in enumerate(keys):
        for c, key in enumerate(row):
            kx = nstart_x + c * ncol_w
            ky = num_start_y + r * nrow_h
            is_accent = (key == "=")
            is_op = key in ["÷", "×", "−", "+"]
            bg = (255, 149, 0, 255) if is_accent else ((40, 48, 64, 255) if is_op else (24, 28, 38, 255))
            text_color = (255, 255, 255) if (is_accent or not is_op) else (255, 158, 30)

            draw.rounded_rectangle([kx, ky, kx + 155, ky + 165], radius=36, fill=bg, outline=(48, 58, 78, 255) if not is_accent else None, width=1)
            b = f_btn.getbbox(key)
            draw.text((kx + (155 - (b[2] - b[0])) // 2, ky + (165 - (b[3] - b[1])) // 2 - 4), key, font=f_btn, fill=text_color)

    return screen

def render_converter_screen():
    size = (840, 1780)
    screen = Image.new("RGBA", size, (16, 20, 27, 255))
    draw = ImageDraw.Draw(screen)

    # Top App Bar
    f_brand = get_font(34)
    draw.text((50, 75), "Unit Converter", font=f_brand, fill=(255, 255, 255, 255))

    # Category Selector Pills (Length, Weight, Temp, Speed, Volume)
    cats = ["Length", "Weight", "Temp", "Speed", "Volume"]
    f_cat = get_font(24)
    cx = 50
    for cat in cats:
        active = (cat == "Length")
        bg = (255, 149, 0, 255) if active else (28, 34, 46, 255)
        tc = (255, 255, 255) if active else (160, 175, 195)
        b = f_cat.getbbox(cat)
        pw = (b[2] - b[0]) + 44
        draw.rounded_rectangle([cx, 160, cx + pw, 224], radius=24, fill=bg)
        draw.text((cx + 22, 176), cat, font=f_cat, fill=tc)
        cx += pw + 18

    # Input Box: Kilometer
    draw.rounded_rectangle([50, 280, 790, 520], radius=32, fill=(22, 27, 38, 255), outline=(48, 58, 78, 255), width=2)
    f_label = get_font(24)
    f_val = get_font(72)
    f_unit = get_font(30)
    draw.text((80, 315), "FROM", font=f_label, fill=(255, 150, 0, 255))
    draw.text((80, 370), "25.5", font=f_val, fill=(255, 255, 255, 255))
    draw.text((600, 400), "Kilometers", font=f_unit, fill=(180, 195, 215, 255))

    # Swap Arrow Icon
    draw.ellipse([380, 500, 460, 580], fill=(255, 149, 0, 255))
    f_arr = get_font(34)
    draw.text((406, 516), "⇅", font=f_arr, fill=(255, 255, 255))

    # Output Box: Miles
    draw.rounded_rectangle([50, 560, 790, 800], radius=32, fill=(22, 27, 38, 255), outline=(48, 58, 78, 255), width=2)
    draw.text((80, 595), "TO (CONVERTED)", font=f_label, fill=(100, 210, 140, 255))
    draw.text((80, 650), "15.845", font=f_val, fill=(255, 255, 255, 255))
    draw.text((660, 680), "Miles", font=f_unit, fill=(180, 195, 215, 255))

    # Keypad for converter
    keys = [
        ["7", "8", "9", "⌫"],
        ["4", "5", "6", "C"],
        ["1", "2", "3", "±"],
        [".", "0", "00", "Done"]
    ]
    f_btn = get_font(38)
    num_start_y = 880
    nrow_h = 200
    ncol_w = 180
    nstart_x = 65

    for r, row in enumerate(keys):
        for c, key in enumerate(row):
            kx = nstart_x + c * ncol_w
            ky = num_start_y + r * nrow_h
            is_accent = (key == "Done")
            bg = (255, 149, 0, 255) if is_accent else (26, 32, 44, 255)
            text_color = (255, 255, 255) if is_accent else (220, 230, 245)

            draw.rounded_rectangle([kx, ky, kx + 155, ky + 160], radius=34, fill=bg, outline=(48, 58, 78, 255) if not is_accent else None, width=1)
            b = f_btn.getbbox(key)
            draw.text((kx + (155 - (b[2] - b[0])) // 2, ky + (160 - (b[3] - b[1])) // 2 - 4), key, font=f_btn, fill=text_color)

    return screen

def render_history_screen():
    size = (840, 1780)
    screen = Image.new("RGBA", size, (16, 20, 27, 255))
    draw = ImageDraw.Draw(screen)

    # Top App Bar
    f_brand = get_font(34)
    draw.text((50, 75), "Calculation History", font=f_brand, fill=(255, 255, 255, 255))
    draw.rounded_rectangle([660, 65, 790, 120], radius=22, fill=(255, 90, 90, 30), outline=(255, 90, 90, 160), width=1)
    f_clr = get_font(20)
    draw.text((685, 80), "Clear All", font=f_clr, fill=(255, 110, 110))

    # Saved history items
    items = [
        ("4,850 × 12.5% + 1,200", "= 1,806.25", "Today, 1:15 PM"),
        ("sin(π / 4) + ln(2.718)", "= 1.7071", "Today, 1:12 PM"),
        ("12,500 / 48", "= 260.4166", "Today, 12:45 PM"),
        ("√(144) + 15²", "= 237", "Yesterday, 6:30 PM"),
        ("180 × 1.15 (Tax calc)", "= 207", "Yesterday, 3:15 PM"),
        ("9,450 − 3,280", "= 6,170", "Oct 30, 11:20 AM"),
    ]

    f_h_expr = get_font(28)
    f_h_res = get_font(44)
    f_h_time = get_font(18)

    y = 170
    for expr, res, ts in items:
        card_box = [50, y, 790, y + 215]
        draw.rounded_rectangle(card_box, radius=28, fill=(22, 27, 38, 255), outline=(45, 55, 75, 255), width=2)
        # Time badge
        draw.text((80, y + 25), ts, font=f_h_time, fill=(140, 155, 175, 255))
        # Expression
        draw.text((80, y + 65), expr, font=f_h_expr, fill=(180, 195, 215, 255))
        # Result
        draw.text((80, y + 115), res, font=f_h_res, fill=(255, 158, 30, 255))
        # Copy icon placeholder on right
        draw.rounded_rectangle([700, y + 115, 755, y + 170], radius=16, fill=(34, 42, 58, 255))
        f_cp = get_font(22)
        draw.text((718, y + 128), "⎘", font=f_cp, fill=(255, 255, 255))
        y += 245

    return screen

def render_privacy_screen():
    size = (840, 1780)
    screen = Image.new("RGBA", size, (16, 20, 27, 255))
    draw = ImageDraw.Draw(screen)

    # Top App Bar
    f_brand = get_font(34)
    draw.text((50, 75), "Security & Privacy", font=f_brand, fill=(255, 255, 255, 255))

    # Main Big Shield Badge
    shield_box = [280, 180, 560, 460]
    draw.ellipse(shield_box, fill=(255, 149, 0, 30), outline=(255, 149, 0, 180), width=3)
    f_shield = get_font(110)
    draw.text((370, 230), "🛡️", font=f_shield, fill=(255, 255, 255))

    f_p_title = get_font(44)
    f_p_sub = get_font(26)
    draw.text((190, 500), "100% Private & Offline", font=f_p_title, fill=(255, 255, 255, 255))
    draw.text((120, 570), "Your calculations never leave this device.", font=f_p_sub, fill=(170, 185, 205, 255))

    # Feature checklist cards
    guarantees = [
        ("🚫 Zero Trackers & Ads", "No third-party SDKs, analytics, or behavioral trackers."),
        ("🔒 On-Device SQLite Storage", "Your calculation history is stored locally in your private sandbox."),
        ("⚡ Lightning Offline Speed", "Works without Wi-Fi or cellular data at maximum performance."),
        ("🛡️ No Dangerous Permissions", "Zero access to contacts, photos, camera, or device ID.")
    ]

    gy = 670
    f_gt = get_font(28)
    f_gd = get_font(22)
    for title, desc in guarantees:
        c_box = [50, gy, 790, gy + 175]
        draw.rounded_rectangle(c_box, radius=26, fill=(22, 27, 38, 255), outline=(48, 58, 78, 255), width=2)
        draw.text((80, gy + 32), title, font=f_gt, fill=(255, 165, 45, 255))
        draw.text((80, gy + 88), desc, font=f_gd, fill=(180, 195, 215, 240))
        gy += 205

    return screen

def generate_screenshots():
    print("Generating 5 High-Definition Play Store Screenshots (1080x2400)...")
    configs = [
        ("1_standard.png", "CLEAN & FLUID MATH", "Instant Calculation", "Real-time expression preview with haptic touch feedback", render_standard_screen),
        ("2_scientific.png", "ADVANCED ENGINEERING", "Scientific Mode", "Full Trigonometry, Logarithms, Roots, Powers & Rad/Deg", render_scientific_screen),
        ("3_converter.png", "SMART CONVERSIONS", "Unit Converter", "Length, Weight, Temperature, Speed & Volume units", render_converter_screen),
        ("4_history.png", "NEVER LOSE EQUATIONS", "History Memory", "Secure SQLite local persistence with 1-tap recall", render_history_screen),
        ("5_privacy.png", "COMPLETE PEACE OF MIND", "100% Private & Offline", "Zero data collection, no trackers, and works everywhere", render_privacy_screen)
    ]

    for filename, badge, title, desc, renderer in configs:
        print(f"Creating screenshot: {filename}...")
        canvas = create_base_screenshot_canvas(badge, title, desc)
        screen_img = renderer()
        draw_phone_frame(canvas, screen_img)
        # Convert to RGB (standard for Play Store)
        final_img = Image.new("RGB", canvas.size, (14, 17, 24))
        final_img.paste(canvas, (0, 0), canvas)
        out_path = os.path.join(SCREENSHOTS_DIR, filename)
        final_img.save(out_path, "PNG", quality=95)
        print(f"Saved: {out_path}")

if __name__ == "__main__":
    generate_icon()
    generate_feature_graphic()
    generate_screenshots()
    print("All Play Store graphics generated successfully!")
