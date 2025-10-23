package main.java.data.logic;

import pos.data.CategoriaDao;
import pos.data.ProductoDao;

import java.util.List;

public class Service {
    private static Service theInstance;

    public static Service instance(){
        if (theInstance == null) theInstance = new Service();
        return theInstance;
    }

    private CategoriaDao categoriaDao;
    private ProductoDao productoDao;

    public Service() {
        try{
            categoriaDao = new CategoriaDao();
            productoDao = new ProductoDao();
        }
        catch(Exception e){
        }
    }

    public void stop(){
    }

    //================= PRPODUCTOS ============
    public void create(Producto e) throws Exception {
        productoDao.create(e);
    }

    public Producto read(Producto e) throws Exception {
        return productoDao.read(e.getCodigo());
    }

    public void update(Producto e) throws Exception {
        productoDao.update(e);
    }

    public void delete(Producto e) throws Exception {
        productoDao.delete(e);
    }

    public List<Producto> search(Producto e) {
        try {
            return productoDao.search(e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    //================= CATEGORIAS ============
    public List<Categoria> search(Categoria e) {
        try {
            return categoriaDao.search(e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

 }
