// init-mongo.js

// Switch to the target database
db = db.getSiblingDB('order_db');

// Cria o usuário da aplicação (com permissões apenas neste DB)
db.createUser({
  user: 'order_user',
  pwd: 'order_password',
  roles: [
    { role: 'readWrite', db: 'order_db' }
  ]
});

// Cria coleção e insere dados iniciais
db.createCollection('products');

db.products.insertMany([
  {
    _id: 'PROD-001',
    name: 'Hamburger Clássico',
    description: 'Hamburger com carne, alface, tomate e queijo',
    price: 25.00,
    category: 'LANCHE',
    createdDate: new Date(),
    updatedDate: new Date()
  },
  {
    _id: 'PROD-002',
    name: 'Coca-Cola 350ml',
    description: 'Refrigerante Coca-Cola lata 350ml',
    price: 5.50,
    category: 'BEBIDA',
    createdDate: new Date(),
    updatedDate: new Date()
  },
  {
    _id: 'PROD-003',
    name: 'Batata Frita',
    description: 'Porção de batata frita crocante',
    price: 12.00,
    category: 'ACOMPANHAMENTO',
    createdDate: new Date(),
    updatedDate: new Date()
  }
]);

db.products.createIndex({ _id: 1 }, { unique: true });
db.products.createIndex({ category: 1 });
db.products.createIndex({ name: "text", description: "text" });

print('✅ MongoDB initialized successfully!');
