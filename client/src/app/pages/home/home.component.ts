import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

interface Product {
  id: string;
  name: string;
  price: number;
  sellerName: string; 
  imageUrls: string[];
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.component.html'
})
export class HomeComponent {
  products: Product[] = [
    {
      id: "ART-M01",
      name: "Cobalt Blue Pure Mineral Pigment (100g)",
      price: 85.00,
      sellerName: "Atelier Oujda",
      imageUrls: ["/dep.png", "/dep.png"]
    },
    {
      id: "ART-M02",
      name: "Hand-Stretched Belgian Linen Canvas (80x100cm)",
      price: 145.50,
      sellerName: "Master Canvas Co.",
      imageUrls: ["/dep.png", "/dep.png"]
    },
    {
      id: "ART-M03",
      name: "Original Oil on Canvas: 'Symmetry of Silence'",
      price: 1200.00,
      sellerName: "Y. Benjelloun Gallery",
      imageUrls: ["/dep.png", "/dep.png"]
    },
    {
      id: "ART-M04",
      name: "Siberian Kolinsky Sable Fine Detail Brush Set",
      price: 195.00,
      sellerName: "Brushes & Ink Node",
      imageUrls: ["/dep.png", "/dep.png"]
    },
    {
      id: "ART-M05",
      name: "Cold-Pressed Organic Walnut Oil Binder (500ml)",
      price: 42.00,
      sellerName: "Alchemist Supply",
      imageUrls: ["/dep.png", "/dep.png"]
    },
    {
      id: "ART-M06",
      name: "Abstract Acrylic Triptych: 'Ethereal Shadows'",
      price: 850.00,
      sellerName: "Studio Zone01",
      imageUrls: ["/dep.png", "/dep.png"]
    },
    {
      id: "ART-M07",
      name: "Professional Beechwood Crank Studio Easel",
      price: 410.00,
      sellerName: "Atelier Oujda",
      imageUrls: ["/dep.png", "/dep.png"]
    },
    {
      id: "ART-M08",
      name: "Raw Italian Burnt Umber Ground Earth",
      price: 38.00,
      sellerName: "Alchemist Supply",
      imageUrls: ["/dep.png", "/dep.png"]
    },
    {
      id: "ART-M09",
      name: "Palette Knife Set - Tempered High-Flex Carbon Steel",
      price: 65.00,
      sellerName: "Brushes & Ink Node",
      imageUrls: ["/dep.png", "/dep.png"]
    },
    {
      id: "ART-M10",
      name: "Impasto Heavy-Body Medium Gesso (1L)",
      price: 54.00,
      sellerName: "Master Canvas Co.",
      imageUrls: ["/dep.png", "/dep.png"]
    },
    {
      id: "ART-M11",
      name: "Watercolor Sheet Pack - 100% Cotton Rag (300gsm)",
      price: 78.50,
      sellerName: "Studio Zone01",
      imageUrls: ["/dep.png", "/dep.png"]
    },
    {
      id: "ART-M12",
      name: "Original Watercolor Portrait: 'The Nomad's Gaze'",
      price: 650.00,
      sellerName: "Y. Benjelloun Gallery",
      imageUrls: ["/dep.png", "/dep.png"]
    }
  ];
}