package com.projetosacademicos.notificacoes;

import java.time.LocalDateTime;
import java.util.List;

public class ProjetoCadastradoEvento {
    private String eventoId;
    private String tipoEvento;
    private Long projetoId;
    private String titulo;
    private Long professorId;
    private List<Long> alunosIds;
    private LocalDateTime dataEvento;

    public ProjetoCadastradoEvento() { }

    public String getEventoId() { return eventoId; }
    public void setEventoId(String eventoId) { this.eventoId = eventoId; }
    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }
    public Long getProjetoId() { return projetoId; }
    public void setProjetoId(Long projetoId) { this.projetoId = projetoId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public Long getProfessorId() { return professorId; }
    public void setProfessorId(Long professorId) { this.professorId = professorId; }
    public List<Long> getAlunosIds() { return alunosIds; }
    public void setAlunosIds(List<Long> alunosIds) { this.alunosIds = alunosIds; }
    public LocalDateTime getDataEvento() { return dataEvento; }
    public void setDataEvento(LocalDateTime dataEvento) { this.dataEvento = dataEvento; }
}
