package com.f1legends.controller.Usuario;

import com.f1legends.DAO.modeloDAO.RankingGlobalDAO;
import com.f1legends.DAO.modeloDAO.UsuarioDAO;
import com.f1legends.modelo.Usuarios.Jugador;
import com.f1legends.modelo.Usuarios.Usuario;

import java.util.List;
import java.util.Optional;

public class UsuarioController {

    private final UsuarioDAO usuarioDAO;
    private final RankingGlobalDAO rankingDAO;

    public UsuarioController(UsuarioDAO usuarioDAO, RankingGlobalDAO rankingDAO) {
        this.usuarioDAO = usuarioDAO;
        this.rankingDAO = rankingDAO;
    }


    public Optional<Usuario> iniciarSesion(String user, String pass) {
        Optional<Usuario> resultado = usuarioDAO.buscarPorUsername(user);
        if (resultado.isEmpty()) return Optional.empty();

        Usuario u = resultado.get();
        if (!u.getPassword().equals(pass)) return Optional.empty();

        Sesion.setUsuarioActual(u);
        u.login();
        return Optional.of(u);
    }


    public void crearPerfil(String user, String pass, String rol) {
        usuarioDAO.crear(user, pass, rol);
    }

    public List<RankingGlobalDAO.EntradaRanking> obtenerRankingGlobal() {
        return rankingDAO.obtenerRankingGlobal();
    }

    public void modificarPerfil(String nuevoUser, String nuevoPass) {
        Usuario sesionActual = Sesion.getUsuarioActual();
        String userFinal = nuevoUser.isBlank() ? sesionActual.getUsername() : nuevoUser;
        String passFinal = nuevoPass.isBlank() ? sesionActual.getPassword() : nuevoPass;

        usuarioDAO.actualizar(sesionActual.getId(), userFinal, passFinal);
        sesionActual.setUsername(userFinal);
        sesionActual.setPassword(passFinal);
    }


    public void eliminarPerfil(int idObjetivo) {
        usuarioDAO.eliminar(idObjetivo);
        Usuario sesionActual = Sesion.getUsuarioActual();
        if (sesionActual != null && sesionActual.getId() == idObjetivo) {
            Sesion.cerrarSesion();
        }
    }

    public List<Usuario> obtenerUsuarios() {
        return usuarioDAO.listarTodos();
    }

    public List<Jugador> obtenerJugadores() {
        return usuarioDAO.listarJugadores();
    }

    public Optional<Usuario> buscarPorId(int id) {
        return usuarioDAO.buscarPorId(id);
    }
}