$gw = 'http://localhost:8081/graphql'

function Invoke-Gql($query, $variables = @{}) {
    $body = @{ query = $query; variables = $variables } | ConvertTo-Json -Depth 10
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($body)
    $r = Invoke-RestMethod -Uri $gw -Method Post -ContentType 'application/json; charset=utf-8' -Body $bytes
    if ($r.errors) { throw ($r.errors | ConvertTo-Json -Depth 5) }
    return $r.data
}

function Novo-Endereco($rua, $numero, $cep) {
    @{ rua = $rua; numero = $numero; cep = $cep; cidade = 'Campina Grande'; estado = 'PB'; pais = 'BR' }
}

$mAluno = 'mutation($input: AlunoInput!) { criarAluno(input: $input) { id } }'
$mProf  = 'mutation($input: ProfessorInput!) { criarProfessor(input: $input) { id } }'
$mProj  = 'mutation($input: ProjetoInput!) { criarProjeto(input: $input) { id } }'

# Alunos
$a1 = (Invoke-Gql $mAluno @{ input = @{ matricula = '202401'; nome = 'Maria Souza'; cpf = '111.111.111-11'; curso = 'Ciência da Computação'; endereco = (Novo-Endereco 'Rua 15 de Novembro' '10' '58400-000') } }).criarAluno.id
$a2 = (Invoke-Gql $mAluno @{ input = @{ matricula = '202402'; nome = 'João Pereira'; cpf = '222.222.222-22'; curso = 'Ciência da Computação'; endereco = (Novo-Endereco 'Rua Sete de Setembro' '25' '58401-000') } }).criarAluno.id
$a3 = (Invoke-Gql $mAluno @{ input = @{ matricula = '202403'; nome = 'Ana Lima'; cpf = '333.333.333-33'; curso = 'Engenharia da Computação'; endereco = (Novo-Endereco 'Av. Floriano Peixoto' '300' '58402-000') } }).criarAluno.id

# Professores
$p1 = (Invoke-Gql $mProf @{ input = @{ matricula = 1001; nome = 'Demetrio Mestre'; curso = 'Ciência da Computação'; endereco = (Novo-Endereco 'Avenida Central' '50' '58410-000') } }).criarProfessor.id
$p2 = (Invoke-Gql $mProf @{ input = @{ matricula = 1002; nome = 'Carlos Almeida'; curso = 'Engenharia da Computação'; endereco = (Novo-Endereco 'Rua do Sol' '77' '58411-000') } }).criarProfessor.id

# Projetos
Invoke-Gql $mProj @{ input = @{ titulo = 'Pesquisa acadêmica'; professorResponsavelId = $p1; alunosIds = [string[]]@($a1, $a2); areaProjeto = 'Sistemas Distribuídos'; resumo = 'Estudo de arquiteturas de microsserviços.'; palavraChave1 = 'microsserviços'; palavraChave2 = 'GraphQL'} } | Out-Null
Invoke-Gql $mProj @{ input = @{ titulo = 'Monitoramento IoT'; professorResponsavelId = $p2; alunosIds = [string[]]@($a3); areaProjeto = 'Internet das Coisas'; resumo = 'Sensores e coleta de dados em tempo real.'; palavraChave1 = 'IoT'; palavraChave2 = 'sensores' } } | Out-Null
Invoke-Gql $mProj @{ input = @{ titulo = 'Análise de Dados Acadêmicos'; professorResponsavelId = $p1; alunosIds = [string[]]@($a1, $a3); areaProjeto = 'Ciência de Dados'; resumo = 'Indicadores de desempenho dos cursos.'; palavraChave1 = 'dados' } } | Out-Null

Write-Host 'Banco populado: 3 alunos, 2 professores, 3 projetos.'