package com.booksgames.loja.controllers;

import com.booksgames.loja.documents.Produto;
import com.booksgames.loja.dto.ProdutoDTO;
import com.booksgames.loja.repository.ProdutoRepository;
import com.booksgames.loja.services.impl.ProdutoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import reactor.core.publisher.Flux;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.List;

import javax.validation.Valid;
import java.net.URI;

import java.util.stream.IntStream;
/**
 * @author Jose R F Junior
 * web2ajax@gmail.com
 * Santiago Chile 15 02 2019
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/produtos")
public class ProdutoController {

  @Autowired
  private ProdutoRepository produtoRepository;

  @Autowired
  private ProdutoServiceImpl produtoServiceImpl;

  @CrossOrigin(origins = "http://localhost:4200")
  @GetMapping(value="/flux")
  public Flux<Produto> getProduto(){
    return produtoServiceImpl.findAll();
  }
  @CrossOrigin(origins = "http://localhost:4200")
  @RequestMapping(value="/flux6", method=RequestMethod.GET)
  public ResponseEntity<List<ProdutoDTO>> findAll() {
    Flux<Produto> listFlux = produtoServiceImpl.findAll();
    List<ProdutoDTO> listDto = listFlux.toStream()
            //.map(obj -> new ProdutoDTO(obj))
            .sorted(Comparator.comparing(Produto::get_id).reversed())
            .map( ProdutoDTO::new)
            .limit(6)
            .collect( Collectors.toList());
    return ResponseEntity.ok().body(listDto);
  }

  @CrossOrigin(origins = "http://localhost:4200")
  @RequestMapping(value="/nome/{descricao}", method=RequestMethod.GET)
  public Flux<Produto> getCorNome(@PathVariable String descricao){
    return produtoServiceImpl.findByDescricao(descricao);
  }

  @RequestMapping(value = "/{_id}", method = RequestMethod.GET)
  public Produto getPetById(@PathVariable("_id") String _id) {
    return produtoRepository.findBy_id(_id);
  }
  
  @RequestMapping(value = "/{_id}", method = RequestMethod.PUT)
  public void modifyPetById(@PathVariable("_id") String _id, @Valid @RequestBody Produto produto) {
    produto.set_id(_id);
    produtoRepository.save(produto);
  }

  @CrossOrigin(origins = "http://localhost:4200")
  @PostMapping(value = "/")
  public ResponseEntity<Void> createProduto(@Valid @RequestBody Produto objDto) {
    Produto obj = produtoServiceImpl.fromDTO(objDto);
    obj = produtoServiceImpl.insert(obj);
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{_id}").buildAndExpand(obj.get_id()).toUri();
    return ResponseEntity.created(uri).build();
  }
  
  @RequestMapping(value = "/{_id}", method = RequestMethod.DELETE)
  public void deletePet(@PathVariable String _id) {
    produtoRepository.delete(produtoRepository.findBy_id(_id));
  }

  @RequestMapping(value="/page", method=RequestMethod.GET)
  public ResponseEntity<Page<ProdutoDTO>> findPage(
          @RequestParam(value="page", defaultValue="0") Integer page,
          @RequestParam(value="linesPerPage", defaultValue="5") Integer linesPerPage,
          @RequestParam(value="orderBy", defaultValue="nome") String orderBy,
          @RequestParam(value="direction", defaultValue="ASC") String direction) {
    Page<Produto> list = produtoServiceImpl.findPage(page, linesPerPage, orderBy, direction);
    Page<ProdutoDTO> listDto = list.map(ProdutoDTO::new);
    return ResponseEntity.ok().body(listDto);
  }
}