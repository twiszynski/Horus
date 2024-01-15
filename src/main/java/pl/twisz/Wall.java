package pl.twisz;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Wall implements Structure {

    private List<Block> blocks;


/* OGÓLNY KOMENTARZ po analizie kodu:
        - metody findBlockByColor(), findBlocksByMaterial() oraz count() wykorzystuja metodę getFlattenListOfBlocks()
          i uwzględniają zarówno elementy CompositeBlock jak i obiekty zawarte w odpowiadających im listach
          List<Block> zwracanych przez metodę getBlocks()
        - w związku z powyższym, zastosowana implementacja metody count() zwraca liczbę wszystkich obiektów jakie zawiera
          lista 'blocks' klasy Wall. Jeżeli mielibyśmy rozpatrywać ten przypadek nie z perspektywy kodu i obiektowości,
          a bardziej jako rzeczywisty przykład konstrukcji ściany, to być może (w zależności od wymagań) należałoby pominąć
          obiekty typu CompositeBlock, a wliczać jedynie ich 'składowe' zwracane przez metodę getBlocks().
          W przypadku takich założeń należałoby zaimplementować metodę count() jak dodaną metodę countWithoutComposite()
        - Podobnie w przypadku właściwości 'color' oraz 'material'.
          CompositeBlock rozszerza Block - dziedziczy metody getColor() oraz getMaterial(), a więc posiada właściwości
          color i material. Z tego względu obecne implementacje metody findBlockByColor() i findBlocksByMaterial()
          uwzględniają w analizie zarówno obiekty CompositeBlock jak i ich 'składowe'.
*/


    /* Metoda spłaszczająca listę blocks obiektu Wall, w przypadku kiedy zawiera ona obiekty typu CompositeBlock
     * (jakie zawierają w sobie kolejne listy).
     * - zwracana lista uwzględnia zarówno dany obiekt CompositeBlock jak i obiekty, które w postaci List<Block>
     *   zwraca odpowiadająca mu metoda getBlocks()
     * - rekurencyjny charakter pozwala na spłaszczenie listy bez względu na poziom zagnieżdżenia
     *   (kolejne listy List<Block> mogą zawierać kolejne obiekty typu CompositeBlock, jakich metoda getBlocks()
     *   zwraca kolejne listy itd... )
     * - metoda pozwala na uniknięcie powielania kodu w metodach findBlockByColor(), findBlocksByMaterial(), count()
     */
    private List<Block> getFlattenListOfBlocks(List<Block> blocks) {
        List<Block> flattenList = new ArrayList<>();
        for (Block block : blocks) {
            flattenList.add(block);
            if (block instanceof CompositeBlock) {
                flattenList.addAll(getFlattenListOfBlocks(((CompositeBlock) block).getBlocks()));
            }
        }
        return flattenList;
    }

    // metoda zwraca strumień ze spłaszczona strukturą - nie wlicza obiektów ComponentBlock, a tylko ich składowe typu Block
    private Stream<Block> getFlattenStreamOfBlocks(List<Block> blocks) {
        return blocks.stream()
                .flatMap(block -> {
                    Stream<Block> flattenStream = Stream.empty();
                    if (block instanceof CompositeBlock) {
                        flattenStream = Stream.concat(flattenStream, getFlattenStreamOfBlocks(((CompositeBlock) block).getBlocks()));
                    } else {
                        flattenStream = Stream.concat(flattenStream, Stream.of(block));
                    }
                    return flattenStream;
        });
    }

    // zwraca dowolny element o podanym kolorze - nie bierze pod uwage elementów CompositeBlock, tylko ich składowe Block
    @Override
    public Optional<Block> findBlockByColor(String color) {
        return getFlattenStreamOfBlocks(blocks).filter(x -> x.getColor().equals(color)).findAny();
    }

    // zwraca wszystkie elementy z danego materiału - nie bierze pod uwage elementów CompositeBlock, tylko ich składowe Block
    @Override
    public List<Block> findBlocksByMaterial(String material) {
        return getFlattenStreamOfBlocks(blocks).filter(x -> x.getMaterial().equals(material)).toList();
    }

    //zwraca liczbę wszystkich elementów tworzących strukturę
    @Override
    public int count() {
        return getFlattenListOfBlocks(blocks).size();
    }

    //zwraca liczbę wszystkich elementów tworzących strukturę - nie wlicza elementów CompositeBlock
    public int countWithoutComposite() {
        return getFlattenStreamOfBlocks(blocks).toList().size();
    }

}
