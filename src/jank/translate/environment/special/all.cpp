#include <jank/translate/environment/special/all.hpp>
#include <jank/parse/expect/type.hpp>

namespace jank
{
  namespace translate
  {
    namespace environment
    {
      namespace special
      {
        std::experimental::optional<cell::cell> handle
        (parse::cell::list const &list, cell::function_body &translated)
        {
          static std::map
          <
            std::string,
            std::function
            <
              cell::cell
              (
                parse::cell::list const &input,
                cell::function_body &output
              )
            >
          > specials
          {
            { "function", &function },
            { "ƒ", &function },
            { "bind", &binding },
            { "return", &return_statement },
            { "if", &if_statement },
            { "do", &do_statement },
          };

          auto &data(list.data);
          if(data.empty())
          { throw std::runtime_error{ "invalid parse list" }; }

          auto const it
          (
            specials.find
            (parse::expect::type<parse::cell::type::ident>(list.data[0]).data)
          );
          if(it != specials.end())
          { return { it->second(list, translated) }; }
          return {};
        }
      }
    }
  }
}

