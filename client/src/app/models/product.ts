export interface Product {
  id?: string; // or 'string' if every product coming from backend always has an ID
  name: string;
  description: string;
  price: number;
  quantity: number;
  userId: string;
  imageUrls: string[];
}