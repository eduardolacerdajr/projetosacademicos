package com.projetosacademicos.projetos;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

@Entity
public class Projeto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column private String titulo;
    @Column private Long professorResponsavelId;
    @ElementCollection
    @CollectionTable(name = "projeto_aluno", joinColumns = @JoinColumn(name = "fk_projeto_id"))
    @Column(name = "fk_aluno_id")
    private List<Long> alunosIds = new ArrayList<>();
    @Column private String areaProjeto;
    @Column(length = 3000) private String resumo;
    @Column private String palavraChave1;
    @Column private String palavraChave2;
    @Column private String palavraChave3;
    @Column private String urlDocumento;

    public Projeto() { }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public Long getProfessorResponsavelId() { return professorResponsavelId; }
    public void setProfessorResponsavelId(Long professorResponsavelId) { this.professorResponsavelId = professorResponsavelId; }
    public List<Long> getAlunosIds() { return alunosIds; }
    public void setAlunosIds(List<Long> alunosIds) { this.alunosIds = alunosIds == null ? new ArrayList<>() : alunosIds; }
    public String getAreaProjeto() { return areaProjeto; }
    public void setAreaProjeto(String areaProjeto) { this.areaProjeto = areaProjeto; }
    public String getResumo() { return resumo; }
    public void setResumo(String resumo) { this.resumo = resumo; }
    public String getPalavraChave1() { return palavraChave1; }
    public void setPalavraChave1(String palavraChave1) { this.palavraChave1 = palavraChave1; }
    public String getPalavraChave2() { return palavraChave2; }
    public void setPalavraChave2(String palavraChave2) { this.palavraChave2 = palavraChave2; }
    public String getPalavraChave3() { return palavraChave3; }
    public void setPalavraChave3(String palavraChave3) { this.palavraChave3 = palavraChave3; }
    public String getUrlDocumento() { return urlDocumento; }
    public void setUrlDocumento(String urlDocumento) { this.urlDocumento = urlDocumento; }
}
