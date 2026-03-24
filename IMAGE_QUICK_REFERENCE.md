# Car Image Standardization - Quick Reference

## 📸 Standard Image Sizes

### List View (All Car Lists)
```
Height: 220px (180px on mobile)
Width: 100%
Class: .car-image or .car-img
```

### Detail Page - Main Image
```
Aspect Ratio: 16:9
Width: 100%
Container: .car-detail-image-wrapper
Image: .car-detail-image
```

### Detail Page - Thumbnails
```
Aspect Ratio: 4:3
Width: 100%
Container: .car-thumbnail-wrapper
Image: .car-thumbnail
```

### Admin Table Thumbnails
```
Size: 90px × 60px
Direct inline style in table context
```

---

## 🎨 CSS Classes (in common.css)

```css
/* List view images */
.car-image, .car-img {
    width: 100%;
    height: 220px;
    object-fit: cover;
    border-radius: 8px;
}

/* Detail main image container */
.car-detail-image-wrapper {
    width: 100%;
    aspect-ratio: 16/9;
    overflow: hidden;
    border-radius: 10px;
}

/* Detail main image */
.car-detail-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

/* Thumbnail container */
.car-thumbnail-wrapper {
    width: 100%;
    aspect-ratio: 4/3;
    overflow: hidden;
    border-radius: 6px;
}

/* Thumbnail image */
.car-thumbnail {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

/* Placeholder for missing images */
.car-image-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #e2e8f0 0%, #cbd5e1 100%);
    color: #64748b;
    font-size: 3rem;
}
```

---

## 📝 HTML Templates

### List View with Fallback
```html
<img th:if="${car.imageUrl != null and !car.imageUrl.isEmpty()}" 
     th:src="${car.imageUrl}" 
     th:alt="${car.name}"
     class="car-img">
<div th:unless="${car.imageUrl != null and !car.imageUrl.isEmpty()}" 
     class="car-img car-image-placeholder">
    <i class="bi bi-car-front"></i>
</div>
```

### Detail Main Image with Fallback
```html
<div class="car-detail-image-wrapper">
    <img th:if="${not #lists.isEmpty(car.images)}" 
         th:src="@{${car.images[0].imageUrl}}" 
         th:alt="${car.name}"
         class="car-detail-image">
    <div th:if="${#lists.isEmpty(car.images)}" 
         class="car-image-placeholder">
        <i class="bi bi-car-front"></i>
    </div>
</div>
```

### Thumbnail Grid
```html
<div class="car-thumbnail-wrapper">
    <img th:src="@{${image.url}}" 
         th:alt="${car.name}"
         class="car-thumbnail">
</div>
```

---

## ✅ Pages Updated

1. ✅ `public/cars.html` - Car listing (220px)
2. ✅ `public/car-detail.html` - Detail page (16:9 main, 4:3 thumbs)
3. ✅ `owner/owner-car-list.html` - Owner cars (220px)
4. ✅ `admin/car-management.html` - Admin table (90×60px)
5. ✅ `common.css` - Centralized styles

---

## 🔧 Quick Fixes

### If image too small/large
→ Check if using `.car-image` or `.car-img` class

### If aspect ratio wrong
→ Ensure using wrapper: `.car-detail-image-wrapper`

### If no placeholder shows
→ Check Thymeleaf condition and icon class

### If layout breaks
→ Verify `object-fit: cover` is applied

---

## 📱 Responsive

- Desktop: 220px height
- Mobile (≤768px): 180px height (auto via common.css)
- Detail page: Maintains aspect ratio on all screens

---

## ✅ Result

**All car images now:**
- Same height in lists (220px)
- Proper aspect ratio in details (16:9)
- Icon placeholders when missing
- No layout breaking
- Consistent across all pages
- Responsive on mobile

**Status: Complete & Production Ready** ✅

