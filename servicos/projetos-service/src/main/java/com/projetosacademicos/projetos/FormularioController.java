package com.projetosacademicos.projetos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
public class FormularioController {
    private final RestTemplate restTemplate;
    private final String alunosUrl;
    private final String professoresUrl;
    private final ProjetoService projetoService;

    public FormularioController(RestTemplate restTemplate,
            @Value("${alunos.service.url}") String alunosUrl,
            @Value("${professores.service.url}") String professoresUrl,
            ProjetoService projetoService) {
        this.restTemplate = restTemplate;
        this.alunosUrl = alunosUrl;
        this.professoresUrl = professoresUrl;
        this.projetoService = projetoService;
    }

    @GetMapping({"/", "/home"})
    public String home() { return "index"; }

    @GetMapping("/formulario-aluno")
    public String formularioAluno() { return "formulario-aluno"; }

    @PostMapping("/formulario-aluno")
    public String salvarAluno(@RequestParam Map<String, String> form, Model model) {
        EnderecoRequest endereco = new EnderecoRequest(form);
        AlunoRequest aluno = new AlunoRequest(form, endereco);
        restTemplate.postForEntity(alunosUrl + "/api/v1/alunos", aluno, Object.class);
        model.addAttribute("mensagem", "Aluno cadastrado com sucesso!");
        return "formulario-aluno";
    }

    @GetMapping("/formulario-professor")
    public String formularioProfessor() { return "formulario-professor"; }

    @PostMapping("/formulario-professor")
    public String salvarProfessor(@RequestParam Map<String, String> form, Model model) {
        EnderecoRequest endereco = new EnderecoRequest(form);
        ProfessorRequest professor = new ProfessorRequest(form, endereco);
        restTemplate.postForEntity(professoresUrl + "/api/v1/professores", professor, Object.class);
        model.addAttribute("mensagem", "Professor cadastrado com sucesso!");
        return "formulario-professor";
    }

    @GetMapping("/formulario-projeto")
    public String formularioProjeto(Model model) {
        carregarParticipantes(model);
        model.addAttribute("projeto", new Projeto());
        return "formulario-projeto";
    }

    @PostMapping("/formulario-projeto")
    public String salvarProjeto(@ModelAttribute Projeto projeto,
            @RequestParam(value = "professorId", required = false) Long professorId,
            @RequestParam(value = "alunosIds", required = false) List<Long> alunosIds,
            Model model) {
        projeto.setProfessorResponsavelId(professorId);
        projeto.setAlunosIds(alunosIds);
        projetoService.cadastrar(projeto);
        model.addAttribute("mensagem", "Projeto cadastrado com sucesso!");
        carregarParticipantes(model);
        model.addAttribute("projeto", new Projeto());
        return "formulario-projeto";
    }

    private void carregarParticipantes(Model model) {
        model.addAttribute("professores", buscarLista(professoresUrl + "/api/v1/professores", ProfessorView[].class));
        model.addAttribute("alunos", buscarLista(alunosUrl + "/api/v1/alunos", AlunoView[].class));
    }

    private <T> List<T> buscarLista(String url, Class<T[]> tipo) {
        try {
            T[] resposta = restTemplate.getForObject(url, tipo);
            return resposta == null ? new ArrayList<>() : Arrays.asList(resposta);
        } catch (RuntimeException indisponivel) {
            return new ArrayList<>();
        }
    }

    public static class EnderecoRequest {
        private String rua, numero, cep, cidade, estado, pais;
        public EnderecoRequest() { }
        EnderecoRequest(Map<String, String> form) {
            rua = form.get("rua"); numero = form.get("numero"); cep = form.get("cep");
            cidade = form.get("cidade"); estado = form.get("estado"); pais = form.get("pais");
        }
        public String getRua() { return rua; } public String getNumero() { return numero; }
        public String getCep() { return cep; } public String getCidade() { return cidade; }
        public String getEstado() { return estado; } public String getPais() { return pais; }
    }
    public static class AlunoRequest {
        private String matricula, nome, cpf, curso;
        private EnderecoRequest endereco;
        public AlunoRequest() { }
        AlunoRequest(Map<String, String> form, EnderecoRequest endereco) {
            matricula = form.get("matricula"); nome = form.get("nome"); cpf = form.get("cpf");
            curso = form.get("curso"); this.endereco = endereco;
        }
        public String getMatricula() { return matricula; } public String getNome() { return nome; }
        public String getCpf() { return cpf; } public String getCurso() { return curso; }
        public EnderecoRequest getEndereco() { return endereco; }
    }
    public static class ProfessorRequest {
        private Integer matricula;
        private String nome, curso;
        private EnderecoRequest endereco;
        public ProfessorRequest() { }
        ProfessorRequest(Map<String, String> form, EnderecoRequest endereco) {
            matricula = form.get("matricula") == null || form.get("matricula").isEmpty() ? null : Integer.valueOf(form.get("matricula"));
            nome = form.get("nome"); curso = form.get("curso"); this.endereco = endereco;
        }
        public Integer getMatricula() { return matricula; } public String getNome() { return nome; }
        public String getCurso() { return curso; } public EnderecoRequest getEndereco() { return endereco; }
    }
    public static class AlunoView {
        private Long id; private String nome;
        public Long getId() { return id; } public String getNome() { return nome; }
        public void setId(Long id) { this.id = id; } public void setNome(String nome) { this.nome = nome; }
    }
    public static class ProfessorView {
        private Long id; private String nome;
        public Long getId() { return id; } public String getNome() { return nome; }
        public void setId(Long id) { this.id = id; } public void setNome(String nome) { this.nome = nome; }
    }
}
