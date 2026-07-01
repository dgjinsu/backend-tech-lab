package com.example.lisalgorithmforfilmlayer.web;

import com.example.lisalgorithmforfilmlayer.diff.DiffResult;
import com.example.lisalgorithmforfilmlayer.diff.Layer;
import com.example.lisalgorithmforfilmlayer.diff.LayerDiffService;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * 화면 하나(index)로 편집 UI와 diff 결과를 모두 보여준다.
 *
 * <p>프론트↔백 계약: before[]/after[] JSON은 항상 <b>bottom-up</b>(index 0 = Baseline).
 * 화면 렌더링만 위→아래로 뒤집어 스크린샷과 같은 모양으로 보인다.</p>
 */
@Controller
public class DiffController {

    private final LayerDiffService diffService;
    private final ObjectMapper objectMapper;

    public DiffController(LayerDiffService diffService, ObjectMapper objectMapper) {
        this.diffService = diffService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public String index(Model model) throws Exception {
        String json = objectMapper.writeValueAsString(sampleStack());
        model.addAttribute("beforeJson", json);
        model.addAttribute("afterJson", json); // 처음엔 before == after (diff 없음)
        return "index";
    }

    @PostMapping("/diff")
    public String diff(@RequestParam String beforeJson,
                       @RequestParam String afterJson,
                       Model model) throws Exception {
        List<Layer> before = objectMapper.readValue(beforeJson, new TypeReference<>() {});
        List<Layer> after = objectMapper.readValue(afterJson, new TypeReference<>() {});

        DiffResult result = diffService.diff(before, after);

        model.addAttribute("beforeJson", beforeJson); // 편집 상태 유지용 재주입
        model.addAttribute("afterJson", afterJson);
        model.addAttribute("result", result);
        return "index";
    }

    /** 기본 스택 — index 0 = Baseline, 그 위로 총 50장 (이름 GKB_LAYER_1..49). */
    private List<Layer> sampleStack() {
        List<Layer> layers = new ArrayList<>();
        layers.add(new Layer("L0", "GKB_SI", 0, 0)); // Baseline
        for (int i = 1; i < 50; i++) {
            layers.add(new Layer("L" + i, "GKB_LAYER_" + i, 0, 0));
        }
        return layers;
    }
}
